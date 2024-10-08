package com.example.greenplate.viewmodels;

import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.greenplate.model.FirebaseDB;
import com.example.greenplate.model.Ingredient;
import com.example.greenplate.model.Recipe;
import com.example.greenplate.views.ShoppingListFormScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingListViewModel extends ViewModel {
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private FirebaseAuth mAuth;

    public ShoppingListViewModel() {
        db = FirebaseDB.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        userRef = db.collection("users").document(email);
    }

    public void addIngredient(String ingredientName, String quantity, String calories, IngredientUpdateCallback callback) {
        if (ingredientName.isEmpty() || quantity.isEmpty()) {
            //null checking
            callback.onIngredientUpdated(false,"Ingredient Name, Quantity, and Units are required");
            return;
        }
        String[] parts = quantity.split(" ");
        double quantityDouble = Double.parseDouble(parts[0]);
        if (quantityDouble < 0) {
            callback.onIngredientUpdated(false, "Quantity cannot be negative");
            return;
        }
        String unit = parts[1];
        if (unit.isEmpty()) {
            callback.onIngredientUpdated(false, "Please enter a unit");
            return;
        }
        double caloriesDouble = Double.parseDouble(calories);
        Ingredient ingredient = new Ingredient(ingredientName, quantityDouble, unit, caloriesDouble, "");

        userRef.collection("list").whereEqualTo("name", ingredientName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Ingredient already exists
                        updateIngredientQuantity(ingredient, quantityDouble, callback);
                    } else {
                        // Ingredient does not exist, proceed to add
                        Map<String, Object> ingredientMap = new HashMap<>();
                        ingredientMap.put("name", ingredientName);
                        ingredientMap.put("quantity", quantityDouble);
                        ingredientMap.put("units", unit);
                        ingredientMap.put("calories", caloriesDouble);
                        userRef.collection("list").document(ingredientName)
                                .set(ingredientMap)
                                .addOnSuccessListener(documentReference -> callback.onIngredientUpdated(true,"Success"))
                                .addOnFailureListener(e -> callback.onIngredientUpdated(false,"Failed to add ingredient: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> callback.onIngredientUpdated(false,"Failed to check if ingredient exists: " + e.getMessage()));

    }

    public void buyIngredient(String ingredientName, String quantity, String calories, IngredientUpdateCallback callback) {
        userRef.collection("list").document(ingredientName)
            .delete()
            .addOnSuccessListener(aVoid -> {
                // Successfully bought ingredient
                // Add to pantry
                createIngredient(ingredientName, quantity, calories, "", callback);
                callback.onIngredientUpdated(true, "Success");
            })
            .addOnFailureListener(e -> {
                // Failed to update quantity
                callback.onIngredientUpdated(false, "Failed to buy ingredient");
            });
    }

    public void createIngredient(
            String ingredientName, String quantity,
            String calories, String expirationdate, IngredientUpdateCallback callback) {

        if (ingredientName.isEmpty() || calories.isEmpty() || quantity.isEmpty()) {
            callback.onIngredientUpdated(false, "Ingredient Name, Quantity, Units, and Calories are required");
            return;
        }

        // Splitting the quantity into numeric value and unit
        String[] parts = quantity.split(" ", 2);  // Splitting with limit to safely handle cases where no space or more than one space might be used.
        if (parts.length < 2) {
            callback.onIngredientUpdated(false, "Quantity must include both a number and a unit.");
            return;
        }

        double quantityDouble;
        try {
            quantityDouble = Double.parseDouble(parts[0]);  // Attempt to parse the quantity part
        } catch (NumberFormatException e) {
            callback.onIngredientUpdated(false, "Quantity must be a valid number.");
            return;
        }

        if (quantityDouble < 0) {
            callback.onIngredientUpdated(false, "Quantity cannot be negative.");
            return;
        }

        String unit = parts[1];
        if (unit.isEmpty()) {
            callback.onIngredientUpdated(false, "Unit cannot be empty.");
            return;
        }

        double caloriesDouble;
        try {
            caloriesDouble = Double.parseDouble(calories);  // Attempt to parse the calories
        } catch (NumberFormatException e) {
            callback.onIngredientUpdated(false, "Calories must be a valid number.");
            return;
        }

        if (caloriesDouble < 0) {
            callback.onIngredientUpdated(false, "Calories cannot be negative.");
            return;
        }

        // Construct the ingredient object
        Ingredient ingredient = new Ingredient(ingredientName, quantityDouble, unit, caloriesDouble, expirationdate);

        // Check if the ingredient already exists in the pantry
        userRef.collection("pantry").whereEqualTo("name", ingredientName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        callback.onIngredientUpdated(false, "Ingredient already exists in the pantry");
                    } else {
                        // Ingredient does not exist, proceed to add
                        Map<String, Object> ingredientMap = new HashMap<>();
                        ingredientMap.put("name", ingredientName);
                        ingredientMap.put("quantity", quantityDouble);
                        ingredientMap.put("units", unit);
                        ingredientMap.put("calories", caloriesDouble);
                        if (!expirationdate.isEmpty()) {
                            ingredientMap.put("expiration date", expirationdate);
                        }

                        userRef.collection("pantry").document(ingredientName)
                                .set(ingredientMap)
                                .addOnSuccessListener(aVoid -> callback.onIngredientUpdated(true, "Ingredient successfully added to pantry"))
                                .addOnFailureListener(e -> callback.onIngredientUpdated(false, "Failed to add ingredient: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> callback.onIngredientUpdated(false, "Failed to check if ingredient exists: " + e.getMessage()));
    }
    public void increaseIngredientQuantity(Ingredient ingredient, IngredientUpdateCallback callback) {
        double newQuantity = ingredient.getQuantity() + 1.0;
        updateIngredientQuantity(ingredient, newQuantity, callback);
    }

    public void decreaseIngredientQuantity(Ingredient ingredient, IngredientUpdateCallback callback) {
        double newQuantity = Math.max(0, ingredient.getQuantity() - 1.0);
        updateIngredientQuantity(ingredient, newQuantity, callback);
    }

    private void updateIngredientQuantity(Ingredient ingredient, Double newQuantity, IngredientUpdateCallback callback) {
        if (newQuantity <= 0) {
            // Remove the ingredient if its quantity becomes 0 or less
            removeIngredient(ingredient, callback);
        } else {
            // Update the ingredient quantity in the database
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("quantity", newQuantity);
            userRef.collection("list").document(ingredient.getName())
                    .update(updateData)
                    .addOnSuccessListener(aVoid -> {
                        // Successfully updated quantity
                        callback.onIngredientUpdated(true, "Successful update");
                    })
                    .addOnFailureListener(e -> {
                        // Failed to update quantity
                        callback.onIngredientUpdated(false, "Failed to update quantity");
                    });
        }
    }

    private void removeIngredient(Ingredient ingredient, IngredientUpdateCallback callback) {
        userRef.collection("list").document(ingredient.getName())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Successfully removed ingredient
                    callback.onIngredientUpdated(true, "Successful removal");
                })
                .addOnFailureListener(e ->
                        callback.onIngredientUpdated(false, "Failed to check if ingredient exists: "
                                + e.getMessage()));
    }

    public void getShoppingList(ShoppingListViewModel.GetListCallback callback) {
        ArrayList<Ingredient> slist = new ArrayList<Ingredient>();
        userRef.collection("list").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot ingredientDocuments = task.getResult();
                for (DocumentSnapshot document : task.getResult()) {
                    Map<String, Object> i = document.getData();
                    Ingredient ingre = new Ingredient(i.get("name").toString(), (double) i.get("quantity"), i.get("units").toString(), (double) i.get("calories"));
                    slist.add(ingre);
                }
                callback.onSuccess(slist);
            } else {
                callback.onFailure("Failed to access list: " + task.getException().getMessage());
            }
        }).addOnFailureListener(e -> System.out.println("Failed with exception: " + e.getMessage()));
    }

    public void getPantry(String recipeName, GetPantryCallBack callback) {
        ArrayList<Ingredient> pantry = new ArrayList<Ingredient>();
        userRef.collection("pantry").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot ingredientDocuments = task.getResult();
                for (DocumentSnapshot document : task.getResult()) {
                    Map<String, Object> i = document.getData();
                    Ingredient ingre = new Ingredient(i.get("name").toString(), (double) i.get("quantity"), i.get("units").toString(), (double) i.get("calories"));
                    pantry.add(ingre);
                }
                callback.onSuccess(pantry);
            } else {
                callback.onFailure("Failed to access pantry: " + task.getException().getMessage());
            }
        }).addOnFailureListener(e -> System.out.println("Failed with exception: " + e.getMessage()));
    }

    public void addIngredientsRecipe(ArrayList<Ingredient> pantry, ArrayList<Recipe> recipes, int recipePosition, AddCallback callback) {
        //Figure out which more ingredients we need
        ArrayList<Ingredient> neededIngredients = new ArrayList<Ingredient>();
        Recipe recipe = recipes.get(recipePosition);
        ArrayList<Ingredient> recipeIngredients = recipe.getIngredients();
        for (Ingredient i : recipeIngredients) {
            String name = i.getName();
            boolean found = false;
            for (Ingredient pI : pantry) {
                if (pI.getName().equalsIgnoreCase(name)) {
                    found = true;
                    //We have some of the ingredient
                    if (pI.getQuantity() < i.getQuantity()) {
                        //We need more of said ingredient
                        addIngredient(name, ((i.getQuantity() - pI.getQuantity()) + " " + i.getUnits()), Double.toString(i.getCalories()), new IngredientUpdateCallback() {
                            @Override
                            public void onIngredientUpdated(boolean success, String error) {
                                if (!success) {
                                    callback.onFailure(error);
                                }
                            }
                        });
                    }
                }
            }
            if (!found) {
                addIngredient(name, i.getQuantity() + " " + i.getUnits(), Double.toString(i.getCalories()), new IngredientUpdateCallback() {
                    @Override
                    public void onIngredientUpdated(boolean success, String error) {
                        if (!success) {
                            callback.onFailure(error);
                        }
                    }
                });
            }
        }
        callback.onSuccess();

    }

    public void buySelectedIngredients(List<Ingredient> ingredients, IngredientUpdateCallback callback) {
        for (Ingredient ingredient : ingredients) {
            buyIngredient(ingredient.getName(), String.valueOf(ingredient.getQuantity()) + " " + ingredient.getUnits(), String.valueOf(ingredient.getCalories()), callback);
        }
    }



    public interface addIngredientCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface IngredientUpdateCallback {
        void onIngredientUpdated(boolean success, String message);
    }

    public interface GetListCallback {
        void onSuccess(ArrayList<Ingredient> pantry);
        void onFailure(String error);
    }

    public interface RecipeCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface AddCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface GetPantryCallBack {
        void onSuccess(ArrayList<Ingredient> pantry);
        void onFailure(String error);
    }

    public static String validateIngredientInput(String ingredientName, String quantity, String calories) {
        System.out.println("Here");
        if (ingredientName.isEmpty() || quantity.isEmpty()) {
            return "Ingredient Name, Quantity, and Units are required";
        }

        String[] parts = quantity.split(" ");
        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            return "Both quantity and unit must be provided";
        }

        try {
            double quantityDouble = Double.parseDouble(parts[0]);
            if (quantityDouble < 0) {
                return "Quantity cannot be negative";
            }
        } catch (NumberFormatException e) {
            return "Quantity must be a valid number";
        }

        if (calories.isEmpty()) {
            return "Calories are required";
        }

        try {
            double caloriesDouble = Double.parseDouble(calories);
            if (caloriesDouble < 0) {
                return "Calories cannot be negative";
            }
        } catch (NumberFormatException e) {
            return "Calories must be a valid number";
        }

        return ""; // No error found
    }


}
