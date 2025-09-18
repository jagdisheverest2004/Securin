package com.example.recipiedata.service;

import com.example.recipiedata.dto.FetchRecipeDTO;
import com.example.recipiedata.dto.FetchRecipesDTO;
import com.example.recipiedata.model.Recipe;
import com.example.recipiedata.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    // For Endpoint 1: Get all recipes (paginated and sorted)
    public FetchRecipesDTO getAllRecipes(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        FetchRecipesDTO  fetchRecipesDTO = new FetchRecipesDTO();
        List<FetchRecipeDTO> fetchRecipesDTOList = recipePage.getContent().stream().map(
                recipe -> {
                    FetchRecipeDTO  fetchRecipeDTO = new FetchRecipeDTO();
                    fetchRecipeDTO.setId(recipe.getId());
                    fetchRecipeDTO.setTitle(recipe.getTitle());
                    fetchRecipeDTO.setCuisine(recipe.getCuisine());
                    fetchRecipeDTO.setRating(recipe.getRating());
                    fetchRecipeDTO.setPrep_time(recipe.getPrep_time());
                    fetchRecipeDTO.setCook_time(recipe.getCook_time());
                    fetchRecipeDTO.setTotal_time(recipe.getTotal_time());
                    fetchRecipeDTO.setDescription(recipe.getDescription());
                    fetchRecipeDTO.setServes(recipe.getServes());
                    fetchRecipeDTO.setNutrients(recipe.getNutrients());
                    return fetchRecipeDTO;
                }
        ).toList();
        fetchRecipesDTO.setFetchRecipeDTOList(fetchRecipesDTOList);
        fetchRecipesDTO.setPageNumber(pageNumber);
        fetchRecipesDTO.setPageSize(pageSize);
        fetchRecipesDTO.setTotalElements(recipePage.getTotalElements());
        fetchRecipesDTO.setTotalPages(recipePage.getTotalPages());
        fetchRecipesDTO.setLastPage(recipePage.isLast());
        return fetchRecipesDTO;
    }

    // For Endpoint 2: Search recipes with dynamic filters
    public FetchRecipesDTO searchRecipes(Specification<Recipe> spec, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Recipe> recipePage = recipeRepository.findAll(spec, pageable);
        FetchRecipesDTO  fetchRecipesDTO = new FetchRecipesDTO();
        List<FetchRecipeDTO> fetchRecipesDTOList = recipePage.getContent().stream().map(
                recipe -> {
                    FetchRecipeDTO  fetchRecipeDTO = new FetchRecipeDTO();
                    fetchRecipeDTO.setId(recipe.getId());
                    fetchRecipeDTO.setTitle(recipe.getTitle());
                    fetchRecipeDTO.setCuisine(recipe.getCuisine());
                    fetchRecipeDTO.setRating(recipe.getRating());
                    fetchRecipeDTO.setPrep_time(recipe.getPrep_time());
                    fetchRecipeDTO.setCook_time(recipe.getCook_time());
                    fetchRecipeDTO.setTotal_time(recipe.getTotal_time());
                    fetchRecipeDTO.setDescription(recipe.getDescription());
                    fetchRecipeDTO.setServes(recipe.getServes());
                    fetchRecipeDTO.setNutrients(recipe.getNutrients());
                    return fetchRecipeDTO;
                }
        ).toList();
        fetchRecipesDTO.setFetchRecipeDTOList(fetchRecipesDTOList);
        fetchRecipesDTO.setPageNumber(pageNumber);
        fetchRecipesDTO.setPageSize(pageSize);
        fetchRecipesDTO.setTotalElements(recipePage.getTotalElements());
        fetchRecipesDTO.setTotalPages(recipePage.getTotalPages());
        fetchRecipesDTO.setLastPage(recipePage.isLast());
        return fetchRecipesDTO;
    }

    public Pageable createPageable(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(Sort.Direction.ASC, sortBy) : Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(pageNumber -1, pageSize,sortByAndOrder);
        return pageable;
    }
}