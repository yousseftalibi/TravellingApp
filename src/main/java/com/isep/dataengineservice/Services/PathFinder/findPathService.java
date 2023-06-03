package com.isep.dataengineservice.Services.PathFinder;


import com.isep.dataengineservice.Models.PathFinder.Graph;
import com.isep.dataengineservice.Models.PathFinder.Node;
import com.isep.dataengineservice.Models.PathFinder.Paths;
import com.isep.dataengineservice.Models.Trip.Place;
import java.util.ArrayList;


public class findPathService {

	//complexity of O(n**3)
	//To use if you do not have a graph object yet
	public static ArrayList<Place> findPath(int minutes, int budget, Place.ApiResponse api) throws CloneNotSupportedException
	{
		Graph graph = new Graph();
		graph.nodesFromPlacesList(api);
		Paths best = findPath(minutes, budget, graph);

		ArrayList<Place> result = new ArrayList<Place>();

		for(int nodeId : best.path)
		{			result.add(api.getFeatures().get(nodeId).getPlace());		}
		return result;
	}

	public static ArrayList<Place> findPathFormatted(int minutes,
													 int budget,
													 Graph graph, Place.ApiResponse api) throws CloneNotSupportedException
	{
		Paths best = findPath(minutes, budget, graph);

		ArrayList<Place> result = new ArrayList<Place>();

		for(int nodeId : best.path)
		{			result.add(api.getFeatures().get(nodeId).getPlace());		}
		return result;
	}

	//complexity of O((n*p) (All the nodes and all the edge.) (worst case scenario : p = n**2)
	//To use with graph Object
	public static Paths findPath(int minutes, int budget,  Graph graph) throws CloneNotSupportedException
	{
		//The program always start considering you are at one monument
		//From this monument it will compute all the possible solution (Branch and Bound Ways)


		//Initialization of the starting point
		ArrayList<Integer> nodeIdList = new ArrayList<Integer>(graph.keys);
		int originalIndex = 0;
		int nodeId = nodeIdList.get(originalIndex++);

		//Initialization of the best path (at 0)
		Paths best = new Paths(minutes, budget);

		//Initialization of the first path (1-)
		int pathIndex = 0;
		ArrayList<Paths> openPaths = new ArrayList<Paths>();
		openPaths.add(new Paths(minutes, budget));
		openPaths.get(pathIndex).add(nodeId);
		openPaths.get(pathIndex).addTodo(graph);


		int lastElement = nodeId;
		Node lastNode = graph.nodeList.get(lastElement);

		int currentElement;
		Node currentNode;

		int nodeListSize = nodeIdList.size();
		while(best.hasToContinue() && originalIndex < nodeListSize)
		{
			//middle while loop is to test that all the path from the last point where tested
			//If a solution hasn't been reach yet, and all the path from the starting point weren't tested yet,
			//then try all the path from the last point where all the path haven't been tested
			while(best.hasToContinue() && pathIndex >= 0)
			{
				lastElement = openPaths.get(pathIndex).path.get(openPaths.get(pathIndex).path.size() - 1);
//				openPaths.get(pathIndex).getPath();

				//Inner while loop test all the paths from the last point reached
				//If a solution hasn't been reach yet, and all the path from the last point weren't tested yet
				//then test them
				while(best.hasToContinue() && openPaths.get(pathIndex).toDo.size() != 0)
				{
					currentElement = openPaths.get(pathIndex).toDo.get(0);
					openPaths.get(pathIndex).toDo.remove(0);

					currentNode = graph.nodeList.get(currentElement);

					if(currentElement != currentNode.getId() || lastElement != lastNode.getId())
					{	lastNode = graph.nodeList.get(lastElement);	}


					if(openPaths.get(pathIndex).canAdd(currentElement, graph.getTime(lastNode, currentNode), graph.getBudget(lastNode, currentNode)))
					{
						openPaths.add(openPaths.get(pathIndex++).clone());
						openPaths.get(pathIndex).add(currentElement);
						openPaths.get(pathIndex).addTodo(graph);
						openPaths.get(pathIndex).addTime(graph.getTime(lastNode, currentNode));
						openPaths.get(pathIndex).addBudget(graph.getBudget(lastNode, currentNode));
						openPaths.get(pathIndex).addDist(graph.getDist(lastNode, currentNode));

						lastElement = currentElement;
						lastNode = graph.nodeList.get(lastElement);
					}

					if(openPaths.get(pathIndex).isBetter(best))
					{	best = openPaths.get(pathIndex).clone();	}
				}
				openPaths.remove(pathIndex--);
			}
			nodeId = nodeIdList.get(originalIndex++);
			openPaths.add(new Paths(minutes,budget));
			openPaths.get(0).add(nodeId);
			openPaths.get(0).addTodo(graph);
			pathIndex = 0;

//			openPaths.get(pathIndex).getPath();
		}

//		System.out.println();
//		System.out.print("Best path to be below or equal to ");
//		System.out.print(minutes);


		//best.getResult(graph);
		return best;
	}
}
