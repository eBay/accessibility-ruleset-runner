//////////////////////////////////////////////////////////////////////////
// Copyright 2017-2019 eBay Inc.
// Author/Developer(s): Scott Izu
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//////////////////////////////////////////////////////////////////////////

package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;

public class ScreenshotsProcessor {
	String screenAbsolutePath = null;
	int HIGHLIGHT_BORDER_LENGTH = 5;
	int MIN_ELEMENT_LENGTH = 10;
	int MAX_ELEMENT_PADDING_LENGTH = 200; // Must be greater than HIGHLIGHT_BORDER_LENGTH
	
	public void createSnapshotForView(WebDriver driver, String fileName) throws Exception{
		// If the driver is RemoteWebDriver then augment it to enable screen shots on it.
		if (driver.getClass().getName().contains("RemoteWebDriver")) {
			driver = new Augmenter().augment(driver);
		}
		screenAbsolutePath = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).getAbsolutePath();
		System.out.println("Reading Screenshot from file: screen.length():"+new File(screenAbsolutePath).length()+" screenAbsolutePath:"+screenAbsolutePath);
		BufferedImage screenImage = ImageIO.read(new File(screenAbsolutePath));
		
		int xLeftScreen = 0;
		int yTopScreen = 0;
		int xRightScreen = screenImage.getWidth();
		int yBottomScreen = screenImage.getHeight();

		// Create a blue border
		Collection<Point> borderPoints = createBorderPointsList(xLeftScreen, yTopScreen, xRightScreen, yBottomScreen, HIGHLIGHT_BORDER_LENGTH);
		borderCreate(screenImage, borderPoints, 0xFF0000FF);
		
		ImageIO.write(screenImage, "png", new File(fileName));
		
		// Must manually do this to avoid java heap space error
		// http://stackoverflow.com/questions/8222224/reading-images-using-imageio-readfile-causes-java-lang-outofmemoryerror-java
		screenImage.flush();
		screenImage = null;
	}

	public void createSnapshot(ScreenShotElementRectangle sser, String fileName) throws Exception{	
		if(screenAbsolutePath == null) {
			return;
		}
		
		BufferedImage screenImage = ImageIO.read(new File(screenAbsolutePath)); // May throw out of memory error
		
		ScreenShotElementRectangle screenRectangle = new ScreenShotElementRectangle(0, 0, screenImage.getWidth(), screenImage.getHeight());			
		ScreenShotElementRectangle elementInScreenRectangle = pushElementInScreenWithBorder(sser, screenRectangle, MIN_ELEMENT_LENGTH, HIGHLIGHT_BORDER_LENGTH, 0); // Make sure inner element rectangle is far enough from border to draw a full outline
		ScreenShotElementRectangle elementViewInScreenRectangle = pushElementInScreenWithBorder(elementInScreenRectangle, screenRectangle, 0, 0, MAX_ELEMENT_PADDING_LENGTH); // Push outer rectangle as far out as possible to create a view of the inner element rectangle
		ScreenShotElementRectangle elementInScreenRectangleRelativeToNewView = offsetRectangleForNewView(elementInScreenRectangle, elementViewInScreenRectangle);

//			System.out.println("sser xLeft:"+sser.getxLeft()+" yTop:"+sser.getyTop()+" xRight:"+sser.getxRight()+" yBottom:"+sser.getyBottom());
//			System.out.println("screenRectangle xLeft:"+screenRectangle.getxLeft()+" yTop:"+screenRectangle.getyTop()+" xRight:"+screenRectangle.getxRight()+" yBottom:"+screenRectangle.getyBottom());
//			System.out.println("elementInScreenRectangle xLeft:"+elementInScreenRectangle.getxLeft()+" yTop:"+elementInScreenRectangle.getyTop()+" xRight:"+elementInScreenRectangle.getxRight()+" yBottom:"+elementInScreenRectangle.getyBottom());
//			System.out.println("elementViewInScreenRectangle xLeft:"+elementViewInScreenRectangle.getxLeft()+" yTop:"+elementViewInScreenRectangle.getyTop()+" xRight:"+elementViewInScreenRectangle.getxRight()+" yBottom:"+elementViewInScreenRectangle.getyBottom());
//			System.out.println("elementInScreenRectangleRelativeToNewView xLeft:"+elementInScreenRectangleRelativeToNewView.getxLeft()+" yTop:"+elementInScreenRectangleRelativeToNewView.getyTop()+" xRight:"+elementInScreenRectangleRelativeToNewView.getxRight()+" yBottom:"+elementInScreenRectangleRelativeToNewView.getyBottom());
		
		// Get sub image for element: xLeft, yTop, width, height
		BufferedImage elementView = screenImage.getSubimage(elementViewInScreenRectangle.getxLeft(), elementViewInScreenRectangle.getyTop(), elementViewInScreenRectangle.getWidth(), elementViewInScreenRectangle.getHeight());
	
		// Create a red border
		Collection<Point> borderPoints = createBorderPointsList(elementInScreenRectangleRelativeToNewView.getxLeft(), elementInScreenRectangleRelativeToNewView.getyTop(), elementInScreenRectangleRelativeToNewView.getxRight(), elementInScreenRectangleRelativeToNewView.getyBottom(), HIGHLIGHT_BORDER_LENGTH);
		borderCreate(elementView, borderPoints, 0xFFFF0000);
		ImageIO.write(elementView, "png", new File(fileName));
		// Undo border since getSubimage changes original screenImage (undo is faster than deep copy)
		borderUndo(elementView, borderPoints);

		// Must manually do this to avoid java heap space error
		// http://stackoverflow.com/questions/8222224/reading-images-using-imageio-readfile-causes-java-lang-outofmemoryerror-java
		screenImage.flush();
		screenImage = null;
		
		elementView.flush();
		elementView = null;
	}

	public ScreenShotElementRectangle offsetRectangleForNewView(
			ScreenShotElementRectangle innerRectangle,
			ScreenShotElementRectangle newOuterRectangle) {
		return new ScreenShotElementRectangle(innerRectangle.getxLeft()-newOuterRectangle.getxLeft(), innerRectangle.getyTop()-newOuterRectangle.getyTop(), innerRectangle.getxRight()-newOuterRectangle.getxLeft(), innerRectangle.getyBottom()-newOuterRectangle.getyTop());
	}

	public ScreenShotElementRectangle pushElementInScreenWithBorder(
			ScreenShotElementRectangle innerRectangle,
			ScreenShotElementRectangle outerRectangle,
			int innerRectangleInnerPadding,
			int innerRectangleOuterPadding,
			int outerRectangleInnerPadding) {
		int xLeftInnerWithContraint = checkContraints  (outerRectangle.getxLeft()+innerRectangleOuterPadding,                            innerRectangle.getxLeft()  -outerRectangleInnerPadding, outerRectangle.getxRight() -innerRectangleOuterPadding-innerRectangleInnerPadding);
		int yTopInnerWithContraint = checkContraints   (outerRectangle.getyTop()+innerRectangleOuterPadding,                             innerRectangle.getyTop()   -outerRectangleInnerPadding, outerRectangle.getyBottom()-innerRectangleOuterPadding-innerRectangleInnerPadding);
		int xRightInnerWithContraint = checkContraints (outerRectangle.getxLeft()+innerRectangleOuterPadding+innerRectangleInnerPadding, innerRectangle.getxRight() +outerRectangleInnerPadding, outerRectangle.getxRight()-innerRectangleOuterPadding);
		int yBottomInnerWithContraint = checkContraints(outerRectangle.getyTop()+innerRectangleOuterPadding+innerRectangleInnerPadding,  innerRectangle.getyBottom()+outerRectangleInnerPadding, outerRectangle.getyBottom()-innerRectangleOuterPadding);
		return new ScreenShotElementRectangle(xLeftInnerWithContraint, yTopInnerWithContraint, xRightInnerWithContraint, yBottomInnerWithContraint);
	}

	private int checkContraints(int lowerBound, int value,
			int upperBound) {
		if(value < lowerBound) {
			return lowerBound;
		} 
		if(upperBound < value) {
			return upperBound;
		}
		return value;
	}

	private Collection<Point> createBorderPointsList(int xLeftO, int yTopO,
			int xRightO, int yBottomO, int width) {
		Set<Point> points = new HashSet<Point>();
		for (int i = 0; i < width; i++) {
			int xLeft = xLeftO - i;
			int yTop = yTopO - i;
			int xRight = xRightO + i;
			int yBottom = yBottomO + i;
			for (int x = xLeft; x <= xRight; x++) { // Top of Rectangle
				points.add(new Point(x, yTop));
			}
			for (int y = yTop; y <= yBottom; y++) { // Right of Rectangle
				points.add(new Point(xRight, y));
			}
			for (int x = xRight; x >= xLeft; x--) { // Bottom of Rectangle
				points.add(new Point(x, yBottom));
			}
			for (int y = yBottom; y >= yTop; y--) { // Left of Rectangle
				points.add(new Point(xLeft, y));
			}
		}
		return points;
	}

	Map<Point, Integer> undoMap = new LinkedHashMap<Point, Integer>();
	private void borderCreate(BufferedImage image,
			Collection<Point> borderPoints, int nCol) {
		for(Point point: borderPoints) {
			int x = point.x;
			int y = point.y;
			try {
				int oCol = image.getRGB(x, y);
				undoMap.put(point, oCol);
				image.setRGB(x, y, nCol);
			} catch (Exception ex) { }
		}
	}

	private void borderUndo(BufferedImage image,
			Collection<Point> borderPoints) {
		for(Point point: borderPoints) {
			int x = point.x;
			int y = point.y;
			try {
				int oCol = undoMap.get(point);
				image.setRGB(x, y, oCol);
			} catch (Exception ex) { }
		}
	}
	
	public static class ScreenShotElementRectangle {
		int xLeft;
		int yTop;
		int xRight;
		int yBottom;
		public ScreenShotElementRectangle(int xLeft, int yTop, int xRight,
				int yBottom) {
			this.xLeft = xLeft;
			this.yTop = yTop;
			this.xRight = xRight;
			this.yBottom = yBottom;
		}
		public ScreenShotElementRectangle(Point p, Dimension d) {
			this.xLeft = p.getX();
			this.yTop = p.getY();
			this.xRight = p.getX()+d.getWidth();
			this.yBottom = p.getY()+d.getHeight();
		}
		public int getxLeft() {
			return xLeft;
		}
		public int getyTop() {
			return yTop;
		}
		public int getxRight() {
			return xRight;
		}
		public int getyBottom() {
			return yBottom;
		}
		public int getWidth() {
			return getxRight() - getxLeft();
		}

		public int getHeight() {
			return getyBottom() - getyTop();
		}
	}
}
