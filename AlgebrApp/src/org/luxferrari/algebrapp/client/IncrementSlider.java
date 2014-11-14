package org.luxferrari.algebrapp.client;

import static com.google.gwt.query.client.GQuery.$;
import static gwtquery.plugins.ui.Ui.Ui;
import static org.luxferrari.algebrapp.client.AlgebrAppGlobals.*;
import gwtquery.plugins.ui.widgets.Slider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.query.client.Function;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class IncrementSlider extends FlowPanel {

	TextBox level = new TextBox();
	Label sliderTitle = new Label();
	Label levelNumber = new Label();
	SimplePanel slider = new SimplePanel();

	public IncrementSlider() {
		super();
		
		level.getElement().setId("level");
		level.setValue(""+INITIAL_SLIDER_VALUE);
		level.setVisible(false);
		levelNumber.getElement().setId("levelLabel");
		levelNumber.getElement().setInnerHTML("1");
		sliderTitle.getElement().setInnerHTML(constants.sliderTitle());
		sliderTitle.setStyleName("sliderTitle");

		slider.getElement().setId("slider");
		
		this.add(level);
		this.add(sliderTitle);
		this.add(levelNumber);
		this.add(slider);
		this.setupElement();
	}

	public Integer getValue() {
		Integer value = Integer.valueOf(level.getValue());
		return value;
	}
	
	public void setValue(Integer value){
		level.setValue(value.toString());
	}

	public void setupElement() {
		int value;
		int position;
		$("#slider", this.getElement()).as(Ui).slider("{ value: " + INITIAL_SLIDER_VALUE +", min: 0, max: "+MAX_LEVEL+", step: 1}")
		.bind(Slider.Event.slide, new Function() {
			@Override
			public boolean f(Event e, Object data) {
				int value;
				int position;
				Slider.Event slideEvent = ((JavaScriptObject) data).cast();
				value = slideEvent.intValue();
				$("#level", this.getElement()).val(""+value);
				position = (100 * value / MAX_LEVEL );
				$("#levelLabel", this.getElement()).text(""+value).css("margin-left", ""+(position-2)+"%").css("margin-right", ""+(98-position)+"%");
				return false;
			}
		})
		.bind(Slider.Event.stop, new Function() {
			@Override
			public boolean f(Event e, Object data) {
				AlgebrApp.refreshExpression();
				return false;
			}
		});
		value = $("#slider", this.getElement()).as(Ui).slider().intValue();
		$("#level").val(""+value);		
		position = (100 * value / MAX_LEVEL);
		$("#levelLabel", this.getElement()).text(""+value).css("margin-left", ""+(position-2)+"%").css("margin-right", ""+(98-position)+"%");	
	}
	
	public void refreshElement(){
		int value = $("#slider", this.getElement()).as(Ui).slider().intValue();
		if(value > MAX_LEVEL) value = MAX_LEVEL;
		int position = (100 * value / MAX_LEVEL);
		$("#slider", this.getElement()).as(Ui).slider("{ value: " + value +", min: 0, max: "+MAX_LEVEL+", step: 1}");
		$("#levelLabel", this.getElement()).text(""+value).css("margin-left", ""+(position-2)+"%").css("margin-right", ""+(98-position)+"%");
		$("#slider a", this.getElement()).css("left", position +"%");
		levelSlider.setValue(value);
	}
}