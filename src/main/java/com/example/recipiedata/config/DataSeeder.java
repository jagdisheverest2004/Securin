//package com.example.recipiedata.config;
//
//import com.example.recipiedata.model.Recipe;
//import com.example.recipiedata.repository.RecipeRepository;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import java.io.InputStream;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class DataSeeder implements CommandLineRunner {
//
//    @Autowired
//    private RecipeRepository recipeRepository;
//
//
//    @Override
//    public void run(String... args) throws Exception {
//        if (recipeRepository.count() == 0) {
//            InputStream inputStream = TypeReference.class.getResourceAsStream("/recipe.json");
//            ObjectMapper mapper = new ObjectMapper();
//            List<Map<String, Object>> recipeData = mapper.readValue(inputStream, new TypeReference<>() {});
//
//            for (Map<String, Object> data : recipeData) {
//                Recipe recipe = new Recipe();
//                recipe.setTitle((String) data.get("title"));
//                recipe.setCuisine((String) data.get("cuisine"));
//                recipe.setDescription((String) data.get("description"));
//                recipe.setServes((String) data.get("serves"));
//                recipe.setRating(parseNumber(data.get("rating"), Float::valueOf));
//                recipe.setPrep_time(parseNumber(data.get("prep_time"), Integer::valueOf));
//                recipe.setCook_time(parseNumber(data.get("cook_time"), Integer::valueOf));
//                recipe.setTotal_time(parseNumber(data.get("total_time"), Integer::valueOf));
//
//                @SuppressWarnings("unchecked")
//                Map<String, String> nutrients = (Map<String, String>) data.get("nutrients");
//                recipe.setNutrients(nutrients);
//
//                recipeRepository.save(recipe);
//            }
//            System.out.println("Database seeded with " + recipeRepository.count() + " recipes.");
//        }
//    }
//
//    private <T extends Number> T parseNumber(Object obj, java.util.function.Function<String, T> parser) {
//        if (obj == null) return null;
//        String str = obj.toString();
//        if (str.equalsIgnoreCase("NaN") || str.trim().isEmpty()) {
//            return null;
//        }
//        return parser.apply(str);
//    }
//}