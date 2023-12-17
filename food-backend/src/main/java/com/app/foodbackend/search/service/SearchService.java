package com.app.foodbackend.search.service;

import com.app.foodbackend.search.model.SearchRequestDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public abstract class SearchService<T> {

    private final EntityManager entityManager;

    public List<T> search(SearchRequestDTO request){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());

        Root<T> root = criteriaQuery.from(getEntityClass());

        List<Predicate> predicates = request.getFilters().stream().map(filter -> {
            Path<?> joinPathExpression = root;
            String pathFieldName = filter.getField();

            if(filter.getField().contains(".")){
                String[] joinPath = filter.getField().split("\\.");

                for (int i=0; i < joinPath.length; ++i) {
                    if(i != joinPath.length -1){
                        // Create a join for each nested property
                        joinPathExpression = ((From<?, ?>) joinPathExpression).join(joinPath[i], JoinType.INNER);
                    }
                    else{
                        pathFieldName = joinPath[i];
                    }
                }
            }

            Predicate predicate = null;

            if(joinPathExpression.get(pathFieldName).getJavaType() == String.class){
                if (filter.getOperator().equals("CONTAINS")) {
                    predicate = criteriaBuilder.like(criteriaBuilder.lower(joinPathExpression.get(pathFieldName)),"%" + filter.getValue().toLowerCase() + "%");
                }
                else if(filter.getOperator().equals("EQUALS")){
                    predicate = criteriaBuilder.equal(criteriaBuilder.lower(joinPathExpression.get(pathFieldName)),filter.getValue().toLowerCase());
                }
            }
            else if(joinPathExpression.get(pathFieldName).getJavaType() == Integer.class){
                Integer fiterValue = Integer.valueOf(filter.getValue());

                if(filter.getOperator().equals("EQUALS")){
                    predicate = criteriaBuilder.equal(joinPathExpression.get(pathFieldName),fiterValue);
                }
                else if (filter.getOperator().equals("GREATER_THAN")){
                    predicate = criteriaBuilder.greaterThan(joinPathExpression.get(pathFieldName),(Comparable)fiterValue);
                }
                else if (filter.getOperator().equals("GREATER_EQUAL")){
                    predicate = criteriaBuilder.greaterThanOrEqualTo(joinPathExpression.get(pathFieldName),(Comparable)fiterValue);
                }
                else if (filter.getOperator().equals("LESS_THAN")){
                    predicate = criteriaBuilder.lessThan(joinPathExpression.get(pathFieldName),(Comparable)fiterValue);
                }
                else if (filter.getOperator().equals("LESS_EQUAL")){
                    predicate = criteriaBuilder.lessThanOrEqualTo(joinPathExpression.get(pathFieldName),(Comparable)fiterValue);
                }
            }


            return predicate;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        criteriaQuery.where(
                criteriaBuilder.and(predicates.toArray(new Predicate[0]))
        );

        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();

    }

    protected abstract Class<T> getEntityClass();
}
