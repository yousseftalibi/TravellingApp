package com.isep.dataengineservice.Models.PathFinder;
import java.util.ArrayList;
import java.util.Collections;

public class Paths implements Cloneable {

	public ArrayList<Integer> path = new ArrayList<Integer>();
	public double time = 0;
	public double dist = 0;
	public int budget = 0;

	public ArrayList<Integer> toDo = new ArrayList<Integer>();
	public int timeToGetCloseTo;
	public int budgetToGetCloseTo;
	public boolean onDistance;

	public Paths(int timeToGet, int budget)
	{
		this.timeToGetCloseTo = timeToGet;
		this.budgetToGetCloseTo = budget;
	}

	//Complexity of O(n)
	public void addTodo(Graph graph)
	{
		this.toDo.add(this.path.get(this.path.size() - 1));
		Node node = graph.nodeList.get(this.path.get(this.path.size() - 1));
		for(Node i : graph.AdjacencyList.get(node).keySet())
		{	this.toDo.add(i.getId());	}
	}


	public int getLast()
	{	return this.path.get(this.path.size() - 1);	}

	public boolean canAdd(int nodeId, double time, double budget)
	{
		int number = Collections.frequency(path, nodeId);
		int lastElement = path.get(path.size()-1);

		if(!(number == 0 || (number == 1 && lastElement == nodeId)))
		{	return false;	}

		boolean timeNextCond = true;
		boolean budgetNextCond = true;

		if(this.timeToGetCloseTo != 0)
		{	timeNextCond = this.time + time <= this.timeToGetCloseTo;	}

		if(this.budgetToGetCloseTo != 0)
		{	budgetNextCond = this.budget + budget <= this.budgetToGetCloseTo; }

		return timeNextCond && budgetNextCond;
	}

	public void add(int nodeId)
	{	this.path.add(nodeId);	}

	public void addTime(double d)
	{	this.time += d;	}

	public  void addBudget(int budget)
	{	this.budget += budget; }

	public void addDist(double d)
	{	this.dist += d; }




	public boolean hasToContinue()
	{
		boolean timeCond = true;
		boolean budgetCond = true;

		if(this.timeToGetCloseTo != 0)
		{	timeCond = this.deltaTimeCond();	}

		if(this.budgetToGetCloseTo != 0)
		{	budgetCond = this.deltaBudgetCond(); }

		return !(timeCond && budgetCond);
	}

	public boolean deltaTimeCond()
	{	return Math.abs(this.timeToGetCloseTo - this.time) < 0.005;	}

	public boolean deltaBudgetCond()
	{	return this.budgetToGetCloseTo - this.budget >= 0;		}

	public boolean isBetter(Paths bestPath)
	{	if(this.path == bestPath.path)
	{	return false;	}

		boolean timeCond = true;
		boolean budgetCond = true;

		if(this.timeToGetCloseTo != 0)
		{	timeCond = this.deltaTimeCond();	}

		if(this.budgetToGetCloseTo != 0)
		{	budgetCond = this.deltaBudgetCond(); }

		return timeCond && budgetCond;
	}


	@SuppressWarnings("unchecked")
	public Paths clone() throws CloneNotSupportedException
	{
		Paths newPath = new Paths(this.timeToGetCloseTo, this.budgetToGetCloseTo);
		newPath.path = (ArrayList<Integer>) this.path.clone();
		newPath.time = this.time;
		newPath.dist = this.dist;
		newPath.budget = this.budget;
		return newPath;
	}

	public void getResult(Graph graph)
	{
		if(this.path.size() <= 0)
		{
			System.out.print("Aucun chemin trouv�");
			return;
		}
		Node lastNode =  graph.nodeList.get(this.path.get(0));
		int currentNode;
		Node node;

		int size = this.path.size();
		for(int i = 1; i < size; i++)
		{
			currentNode = this.path.get(i);
			node = graph.nodeList.get(currentNode);

			System.out.print("Monument : ");
			System.out.print(lastNode.getName());
			System.out.print(" to ");
			System.out.print(node.getName());

			System.out.print(" temps :");
			System.out.print(graph.AdjacencyList.get(lastNode).get(node).timeSpent);
			lastNode = node;
			System.out.println();
		}

		System.out.print("Total time out : ");
		System.out.println(this.time);
		System.out.print("Total expense out : ");
		System.out.println(this.budget);
		System.out.print("Total distance out : ");
		System.out.println(this.dist);
		System.out.println("");
	}


	public void getPath()
	{
		for(int i : this.path)
		{
			System.out.print(i);
			System.out.print('-');
		}
		System.out.println();
	}
}
