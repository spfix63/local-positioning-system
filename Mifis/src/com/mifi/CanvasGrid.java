package com.mifi;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class CanvasGrid
{
	private int x, y;
	private int gridWidth, gridHeight, cellWidth, cellHeight;
	private int verticalCells, horizontalCells;
	
	CanvasGrid(int horizontalCells, int verticalCells, int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.horizontalCells = horizontalCells;
		this.verticalCells = verticalCells;
		
		
		cellWidth = width/horizontalCells;
		cellHeight = height/verticalCells;

		gridWidth = cellWidth * horizontalCells;
		gridHeight = cellHeight * horizontalCells;
		
	}
	
	public void draw(Canvas c, Paint p)
	{

		p.setStrokeWidth(cellWidth / 10);
		for (int i = 0, yy = y; i <= verticalCells; i++, yy += cellHeight)
		{
			c.drawLine(x, yy, x + gridWidth, yy, p);
		}
		
		for (int i = 0, xx = x; i <= horizontalCells; i++, xx += cellWidth)
		{
			c.drawLine(xx, y, xx, y + gridHeight, p);
		}
	}

	public void adjustToIntersection(Point point)
	{
		if (point.x < x)
			point.x = x;
		else if (point.x > x + gridWidth)
			point.x = x + gridWidth;
		else
			point.x = x + (point.x - x + cellWidth/2) / cellWidth * cellWidth;
		
		if (point.y < y)
			point.y = y;
		else if (point.y > y + gridHeight)
			point.y = y + gridHeight;
		else
			point.y = y + (point.y - y + cellHeight/2) / cellHeight * cellHeight;
	}
	
	public void adjustToMiddle(Point point)
	{
		if (point.x < x)
			point.x = x;
		else if (point.x > x + gridWidth)
			point.x = x + gridWidth;
		else
			point.x = x + (point.x - x) / cellWidth * cellWidth;
		
		if (point.y < y)
			point.y = y;
		else if (point.y > y + gridHeight)
			point.y = y + gridHeight;
		else
			point.y = y + (point.y - y) / cellHeight * cellHeight;

		point.x += cellWidth/2;
		point.y += cellHeight/2;
	}

	public int getLeft()
	{
		return x;
	}

	public int getTop()
	{
		return y;
	}

	public int getCellWidth()
	{
		return cellWidth;
	}

	public int getCellHeight()
	{
		return cellHeight;
	}

	public Point getPosition(Point p)
	{
		int xx = (p.x - x) / cellWidth;
		int yy = (p.y - y) / cellHeight;
		return new Point(xx, yy);
	}
}
