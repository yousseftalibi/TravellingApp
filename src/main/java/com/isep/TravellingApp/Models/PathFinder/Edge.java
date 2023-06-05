package com.isep.TravellingApp.Models.PathFinder;
public class Edge {
	
	public int nodeOne;
	public int nodeTwo;
	
	public double timeSpent;
	public double distance;
	
	public int budget;
	
	public Edge(int NodeOne, int NodeTwo, double time, double dist, int budget)
	{
		this.nodeOne = NodeOne;
		this.nodeTwo = NodeTwo;
		this.timeSpent = time;
		this.distance = dist;
		this.budget = budget;
	}
	
}
