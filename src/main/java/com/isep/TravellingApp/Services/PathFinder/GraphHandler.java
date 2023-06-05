package com.isep.TravellingApp.Services.PathFinder;

import com.isep.TravellingApp.Models.Trip.Place;
import com.isep.TravellingApp.Models.PathFinder.Graph;
public class GraphHandler {

	public static Graph graphFromApiResponse(Place.ApiResponse Places)
	{
		Graph graph = new Graph();
		graph.nodesFromPlacesList(Places);
		return graph;
	}
}
