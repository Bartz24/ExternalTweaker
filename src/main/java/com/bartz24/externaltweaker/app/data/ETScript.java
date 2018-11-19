package com.bartz24.externaltweaker.app.data;

import java.util.ArrayList;
import java.util.List;

public class ETScript {
	public String filePath;
	public String fileName;
	public List<ETActualRecipe> recipes = new ArrayList();

	public ETScript(String filePath, String fileName) {
		this.fileName = fileName;
		this.filePath = filePath;
	}
	
	public ETScript clone()
	{
		ETScript s = new ETScript("", "");
		s.fileName=fileName;
		s.filePath=filePath;
		s.recipes.addAll(recipes);
		return s;
	}
}
