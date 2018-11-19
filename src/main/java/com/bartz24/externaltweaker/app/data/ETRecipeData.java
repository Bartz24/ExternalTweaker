package com.bartz24.externaltweaker.app.data;

import java.io.Serializable;
import java.util.List;

import com.bartz24.externaltweaker.app.Strings;

public class ETRecipeData implements Serializable {
	private String recipeFormat;
	private String[] paramNames;
	private boolean addRecipe;

	public ETRecipeData(String recipe, String[] parameterNames, boolean isAdd) {
		recipeFormat = recipe;
		paramNames = (parameterNames.length == 0 ? new String[getParameterCount()] : parameterNames);
		addRecipe = isAdd;
	}

	public ETRecipeData(String recipe, List<String> parameterNames, boolean isAdd) {
		recipeFormat = recipe;
		paramNames = (parameterNames.size() == 0 ? new String[getParameterCount()]
				: parameterNames.toArray(new String[getParameterCount()]));
		addRecipe = isAdd;
	}

	public int getParameterCount() {
		return getParameterTypes().length;
	}

	public int getParameterCountOptMin() {
		int num = getParameterCount();
		for (String s : getParameterTypes())
			if (s.startsWith("optional."))
				num--;
		return num;
	}

	public String[] getParameterTypes() {
		return recipeFormat.substring(recipeFormat.indexOf("(") + 1, recipeFormat.length() - 2).split(", ");
	}

	public String getRecipeDisplay() {
		String s = recipeFormat.substring(0, recipeFormat.indexOf("(") + 1);
		for (int i = 0; i < getParameterCount(); i++) {
			if (i >= paramNames.length || Strings.isNullOrEmpty(paramNames[i]))
				s += getParameterTypes()[i].trim();
			else
				s += paramNames[i].trim();
			s += ", ";
		}
		return s.substring(0, s.length() - 2) + ");";
	}

	public String getRecipeFormat() {
		return recipeFormat;
	}

	public void setParamName(int index, String name) {
		paramNames[index] = name;
	}

	public String getParamName(int index) {
		return paramNames[index];
	}

	public boolean isAddRecipe() {
		return addRecipe;
	}

	public void setAddRecipe(boolean isAddRecipe) {
		addRecipe = isAddRecipe;
	}
}
