package com.bartz24.externaltweaker.app.data;

import com.bartz24.externaltweaker.app.Strings;

public class ETActualRecipe {
	private String recipeFormat;
	private String[] parameters;

	public ETActualRecipe(String format, String... curParameters) {
		recipeFormat = format;		
		parameters = curParameters;
	}

	public String recipeToString(ETRecipeData linkedRecipe) {
		String base = getRecipeFormat();
		String string = base.substring(0, base.indexOf("(") + 1);
		for (int i = 0; i < parameters.length; i++) {		
			if (!Strings.isNullOrEmpty(parameters[i])) {
				if (parameters[i].equals("~")) {
					string = string.substring(0, string.length() - 2);
					break;
				} else {
					string += parameters[i];
				}
			} else {
				string += linkedRecipe.getParameterTypes()[i];
			}
			if (i < parameters.length - 1)
				string += ", ";

		}
		return string + ");";
	}

	public String getRecipeFormat() {
		return recipeFormat;
	}

	public String getParameterData(int index) {
		return parameters[index];
	}

	public void setParameterData(int index, String value) {
		parameters[index] = value;
	}

	public ETActualRecipe clone() {
		ETActualRecipe r = new ETActualRecipe("", new String[this.parameters.length]);
		r.recipeFormat = this.recipeFormat;
		for (int i = 0; i < this.parameters.length; i++)
			r.setParameterData(i, this.parameters[i]);
		return r;
	}
}
