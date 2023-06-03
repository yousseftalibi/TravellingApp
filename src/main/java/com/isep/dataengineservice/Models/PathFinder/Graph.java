package com.isep.dataengineservice.Models.PathFinder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.isep.dataengineservice.Models.PathFinder.Edge;
import com.isep.dataengineservice.Models.PathFinder.Node;
import com.isep.dataengineservice.Models.Trip.Place;


public class Graph {

	public HashMap<Integer,Node> nodeList = new HashMap<Integer, Node>();
	public HashMap<Node,HashMap<Node, Edge>> AdjacencyList = new HashMap<Node,HashMap<Node,Edge>>();
	public Set<Integer> keys;
	public HashMap<String,Place> PlacesDict = new HashMap<String,Place>();

	public Graph()
	{}

	//Complexity of O(n**2)
	public void nodesFromPlacesList(Place.ApiResponse PlaceList)
	{
		List<Place.Feature> features = PlaceList.getFeatures();
		int listSize = features.size();

		Random r = new Random();

		Node nodeOne;
		Node nodeTwo;

		Edge edge;

		double time;
		double dist;
		int budget;

		for(int i = 0; i < listSize; i++)
		{
			nodeOne = new Node(i,features.get(i).getPlace().getName());
			this.AdjacencyList.put(nodeOne, new HashMap<Node,Edge>());
			this.nodeList.put(i, nodeOne);

			for(int j = i; j < listSize; j++)
			{
				nodeTwo = new Node(j,features.get(j).getPlace().getName());


				budget = r.nextInt(5);
				if(i == j)
				{	dist = 0;	time = r.nextInt(91);}
				else
				{
					dist = this.haversineDistance(features.get(i).getCoordinates().getCoordinates(),
							features.get(j).getCoordinates().getCoordinates());
					time = dist*(1 + r.nextInt(6))/300;
				}

				edge = new Edge(i,j,time,dist,budget);

				this.AdjacencyList.get(nodeOne).put(nodeTwo,edge);

				if(!this.AdjacencyList.containsKey(nodeTwo))
				{	this.AdjacencyList.put(nodeTwo, new HashMap<Node,Edge>());}
				this.AdjacencyList.get(nodeTwo).put(nodeOne,edge);
			}
		}
		this.keys = this.nodeList.keySet();
	}

	//Complexity of O(1)
	public double haversineDistance(List<Double> coordOne, List<Double> coordTwo)
	{
		//https://www.movable-type.co.uk/scripts/latlong.html
		double radians = Math.PI / 180;

		double phi1 = coordOne.get(0) * radians;
		double phi2 = coordTwo.get(0) * radians;

		double lambda1 = coordOne.get(1) * radians;
		double lambda2 = coordTwo.get(1) * radians;

		double a = Math.pow(Math.sin((phi2-phi1)/2),2)
				+ Math.cos(phi1)*Math.cos(phi2)
				*Math.pow(Math.sin((lambda2-lambda1)/2), 2);

		double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return 6371000*c;
	}

	public void nodesPath(String path) throws IOException
	{
		// The file in that location is opened
		FileReader f = new FileReader(path);
		BufferedReader b = new BufferedReader(f);

		boolean notEmpty = true;
		String line = "";
		String[] values = new String[2];
		Node node;
		int id;

		// Our matrix is filled with the values of the file
		while(notEmpty)
		{
			line = b.readLine();
			if(line != null)
			{
				values = line.trim().split("\\s+");
				id = Integer.parseInt(values[0]);
				node = new Node(id,values[1]);

				//System.out.print(id);System.out.print(' ');System.out.println(node.getId());
				this.AdjacencyList.put(node, new HashMap<Node,Edge>());
				this.nodeList.put(id, node);
			}
			else
			{	notEmpty = false; }
		}
		this.keys = this.nodeList.keySet();
		// Closing file
		b.close();
	}

	public void edgesPath(String path) throws IOException
	{
		// The file in that location is opened
		FileReader f = new FileReader(path);
		BufferedReader b = new BufferedReader(f);

		boolean notEmpty = true;
		String line = "";
		String[] values = new String[5];
		int idOne;
		int idTwo;
		int time;
		int dist;
		int budget;

		Node nodeOne;
		Node nodeTwo;

		Edge edge;
		// Our matrix is filled with the values of the file matrix
		while(notEmpty)
		{
			line = b.readLine();
			if(line != null)
			{
				values = line.trim().split("\\s+");
				idOne = Integer.parseInt(values[0]);
				idTwo = Integer.parseInt(values[1]);
				dist = Integer.parseInt(values[2]);
				time = Integer.parseInt(values[3]);
				budget = Integer.parseInt(values[4]);

				nodeOne = this.nodeList.get(idOne);
				nodeTwo = this.nodeList.get(idTwo);

				edge = new Edge(idOne,idTwo,time,dist,budget);

				this.AdjacencyList.get(nodeOne).put(nodeTwo,edge);
				this.AdjacencyList.get(nodeTwo).put(nodeOne,edge);
			}
			else
			{	notEmpty = false;	}
		}
		// Closing file
		b.close();
	}

	//Complexity of O(1)
	public double getTime(Node nodeOne, Node nodeTwo)
	{	return this.AdjacencyList.get(nodeOne).get(nodeTwo).timeSpent;	}
	//Complexity of O(1)
	public Integer getBudget(Node nodeOne, Node nodeTwo)
	{	return this.AdjacencyList.get(nodeOne).get(nodeTwo).budget;	}
	//Complexity of O(1)
	public double getDist(Node nodeOne, Node nodeTwo)
	{	return this.AdjacencyList.get(nodeOne).get(nodeTwo).distance;	}
}
