package com.isep.dataengineservice.Services.PathFinder;



import com.isep.dataengineservice.Models.Trip.Place;
import com.isep.dataengineservice.Models.PathFinder.Graph;
public class GraphHandler {

	public static Graph graphFromApiResponse(Place.ApiResponse Places)
	{
		Graph graph = new Graph();
		graph.nodesFromPlacesList(Places);
		return graph;
	}
}
