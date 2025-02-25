package org.luxferrari.algebrapp.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

public class DragMoveAnimation extends Animation{	// Inutilizzato
    private final Element element;
    private int startX;
    private int startY;
    private int finalX;
    private int finalY;

    public DragMoveAnimation(Element element)
    {
	this.element = element;
    }

    public void scrollTo(int x, int y, int milliseconds)
    {
	this.finalX = x;
	this.finalY = y;

	startX = element.getOffsetLeft();
	startY = element.getOffsetTop();

	run(milliseconds);
    }

    @Override
    protected void onUpdate(double progress)
    {
	double positionX = startX + (progress * (this.finalX - startX));
	double positionY = startY + (progress * (this.finalY - startY));

	this.element.getStyle().setLeft(positionX, Style.Unit.PX);
	this.element.getStyle().setTop(positionY, Style.Unit.PX);
    }

    @Override
    protected void onComplete()
    {
	super.onComplete();
	this.element.getStyle().setLeft(this.finalX, Style.Unit.PX);
	this.element.getStyle().setTop(this.finalY, Style.Unit.PX);
    }
}
