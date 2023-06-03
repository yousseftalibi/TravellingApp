package com.isep.dataengineservice.Models.PathFinder;

public class Node implements Comparable<Node>{
	public int Id;
	public String Name;
	
	public Node(int Id, String Name)
	{
		this.Id = Id;
		this.Name = Name;
	}
	
	public int getId() { return this.Id; }
	public String getName() { return this.Name; } 
	
	@Override
	public int compareTo(Node n)
	{
		return this.Id == n.getId() ? 0 : 
			this.Id > n.getId() ? 1 : - 1;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this) {
            return true;
        } else if (!(o instanceof Node))
		{
			return false;
		}
		
		Node n = (Node) o;
		return this.Id == n.getId();	
	}
	
	@Override
	public int hashCode()
	{	return this.Id; }
}
