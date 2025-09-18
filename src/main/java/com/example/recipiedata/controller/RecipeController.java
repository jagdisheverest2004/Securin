package com.example.recipiedata.controller;

import com.example.recipiedata.config.AppConstants;
import com.example.recipiedata.dto.FetchRecipesDTO;
import com.example.recipiedata.model.Recipe;
import com.example.recipiedata.service.RecipeService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/fetch-all")
    public ResponseEntity<?> getAllRecipes(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false)  Integer pageSize,
            @RequestParam(name = "sortBy" , defaultValue = AppConstants.SORT_RECIPES_BY,required = false) String sortBy,
            @RequestParam(name = "sortOrder" , defaultValue = AppConstants.SORT_RECIPES_DIR,required = false) String sortOrder
    ) {
        FetchRecipesDTO fetchRecipesDTO = recipeService.getAllRecipes(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(fetchRecipesDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRecipes(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String calories,
            @RequestParam(required = false) String total_time,
            @RequestParam(required = false) String rating,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false)  Integer pageSize,
            @RequestParam(name = "sortBy" , defaultValue = AppConstants.SORT_RECIPES_BY,required = false) String sortBy,
            @RequestParam(name = "sortOrder" , defaultValue = AppConstants.SORT_RECIPES_DIR,required = false) String sortOrder
    ) {

        Specification<Recipe> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (cuisine != null && !cuisine.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("cuisine"), cuisine));
            }

            addNumericPredicate(predicates, "rating", rating, criteriaBuilder, root);
            addNumericPredicate(predicates, "total_time", total_time, criteriaBuilder, root);

            addJsonbCaloriePredicate(predicates, calories, criteriaBuilder, root);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        FetchRecipesDTO fetchRecipesDTO = recipeService.searchRecipes(spec,pageNumber,pageSize,sortBy,sortOrder);
        return ResponseEntity.ok(fetchRecipesDTO);
    }

    private void addNumericPredicate(List<Predicate> predicates, String field, String filter,
                                     CriteriaBuilder cb, Root<Recipe> root) {
        if (filter == null || filter.trim().isEmpty()) return;

        Pattern pattern = Pattern.compile("([<>]=?|=)(\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(filter.trim());

        if (matcher.find()) {
            String operator = matcher.group(1);
            double value = Double.parseDouble(matcher.group(2));
            Expression<Double> fieldExpression = root.get(field);

            switch (operator) {
                case ">"  -> predicates.add(cb.greaterThan(fieldExpression, value));
                case ">=" -> predicates.add(cb.greaterThanOrEqualTo(fieldExpression, value));
                case "<"  -> predicates.add(cb.lessThan(fieldExpression, value));
                case "<=" -> predicates.add(cb.lessThanOrEqualTo(fieldExpression, value));
                case "="  -> predicates.add(cb.equal(fieldExpression, value));
            }
        } else {
            try {
                double value = Double.parseDouble(filter.trim());
                predicates.add(cb.equal(root.get(field), value));
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format for filter '" + field + "': " + filter);
            }
        }
    }

    private void addJsonbCaloriePredicate(List<Predicate> predicates, String filter, CriteriaBuilder cb, Root<Recipe> root) {
        if (filter == null || filter.trim().isEmpty()) return;

        Pattern pattern = Pattern.compile("([<>]=?|=)(\\d+)");
        Matcher matcher = pattern.matcher(filter.trim());

        if (matcher.find()) {
            String operator = matcher.group(1);
            int value = Integer.parseInt(matcher.group(2));
            Expression<String> calorieTextExpr = cb.function(
                    "jsonb_extract_path_text",
                    String.class,
                    root.get("nutrients"),
                    cb.literal("calories")
            );
            Expression<String> numericOnlyExpr = cb.function(
                    "regexp_replace",
                    String.class,
                    calorieTextExpr,
                    cb.literal("[^0-9]"),
                    cb.literal("")
            );
            Expression<Integer> calorieIntExpr = numericOnlyExpr.as(Integer.class);
            switch (operator) {
                case ">"  -> predicates.add(cb.greaterThan(calorieIntExpr, value));
                case ">=" -> predicates.add(cb.greaterThanOrEqualTo(calorieIntExpr, value));
                case "<"  -> predicates.add(cb.lessThan(calorieIntExpr, value));
                case "<=" -> predicates.add(cb.lessThanOrEqualTo(calorieIntExpr, value));
                case "="  -> predicates.add(cb.equal(calorieIntExpr, value));
            }
        }
    }
}