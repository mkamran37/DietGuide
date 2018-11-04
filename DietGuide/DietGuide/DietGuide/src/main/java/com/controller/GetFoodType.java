package com.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.model.FileChangeListener;
import com.model.ProductRanking;
import com.model.bean.FoodOptions;
public class GetFoodType extends HttpServlet {

	private static final long serialVersionUID = 1L;
	FileChangeListener listener = FileChangeListener.getInstance();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	@Override
	public void init() throws ServletException {
		super.init();
		System.out.println("initializing");
		ProductRanking.getInstance().loadAllProducts();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Food type");
		String foodType = (String) request.getParameter("foodType");
		System.out.println(foodType);
		FoodOptions options = new FoodOptions();
		options.setFoodCategories(listener.getCategories(foodType));
		options.setFoodAllergies(listener.getAllergies(foodType));
		response.setContentType("json");
		response.setHeader("Content-Type", "Access-Control-Allow-Headers");
		PrintWriter writer = response.getWriter();
		System.out.println(writer);
		System.out.println(new Gson().toJson(options));
		writer.append(new Gson().toJson(options));
		writer.flush();
	}
}