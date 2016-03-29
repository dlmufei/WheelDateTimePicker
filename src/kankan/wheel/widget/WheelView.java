/*
 *  Android Wheel Control.
 *  https://code.google.com/p/android-wheel/
 *  
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package kankan.wheel.widget;

import java.util.LinkedList;
import java.util.List;

import com.example.wheeldatetimepicker.R;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Numeric wheel view.
 * 
 * @author Yuri Kanivets
 */
public class WheelView extends View {
	/** 
	 * Scrolling duration 
	 * 
	 *  </br>
	 *
	 */
	private static final int SCROLLING_DURATION = 400;

	/** 
	 * Minimum delta for scrolling 
	 * 
	 *  </br>
	 * 
	 */
	private static final int MIN_DELTA_FOR_SCROLLING = 1;

	/** 
	 * Current value & label text color 
	 * 
	 *  </br>
	 * 
	 */
	private static final int VALUE_TEXT_COLOR = 0xF0000000;

	/** 
	 * 未选中部分的字体颜色
	 * 
	 *  </br>
	 *
	 */
	private static final int ITEMS_TEXT_COLOR = 0xFFCFCFCF;

	/** 
	 * Top and bottom shadows colors 
	 * 
	 *  </br>
	 * 
	 * 
	 */
	private static final int[] SHADOWS_COLORS = new int[] { 0x00FFFFFF,
			0x00FFFFFF, 0x00FFFFFF };

	/** 
	 * Additional items height (is added to standard text item height) 
	 * 
	 *  </br>
	 * ������ĸ߶���ĸ߶�  (��λӦ����dp) </br>
	 * �Ӻ���getDesiredHeight() �������������ֵӦ����ƽ�ָ�ÿһ��ѡ��ġ� </br>
	 * �����������о�ɣ���������һ���ܺͣ�Ҳ������5���ɼ����ôÿ���ɼ���ĸ��Ӹ߾��� ADDITIONAL_ITEM_HEIGHT/5
	 */
	private static final int ADDITIONAL_ITEM_HEIGHT = 50;

	/** 
	 * Text size 
	 * 
	 *  </br>
	 * �ֺ�
	 */
	private static final int TEXT_SIZE = 28;

	/** 
	 * Top and bottom items offset (to hide that) 
	 * 
	 *  </br>
	 */
	private static final int ITEM_OFFSET = TEXT_SIZE / 7;

	/** 
	 * Additional width for items layout 
	 * 
	 *  </br>	
	 */
	private static final int ADDITIONAL_ITEMS_SPACE = 10;

	/** 
	 * Label offset
	 * 
	 *  </br>
	 */
	private static final int LABEL_OFFSET = 8;

	/** 
	 * Left and right padding value
	 * 
	 *  </br>
	 */
	private static final int PADDING = 10;

	/** 
	 * Default count of visible items 
	 * 
	 *  </br>
	 */
	private static final int DEF_VISIBLE_ITEMS = 5;

	// Wheel Values
	/**
	 * Wheel Values
	 * 
	 *  </br>
	 */
	private WheelAdapter adapter = null;
	/**
	 * Wheel Values
	 * 
	 *  </br>
	 */
	private int currentItem = 0;
	
	// Widths
	/**
	 * Widths
	 * 
	 *  </br>
	 */
	private int itemsWidth = 0;
	/**
	 * Widths
	 * 
	 *  </br>
	 */
	private int labelWidth = 0;

	// Count of visible items
	/**
	 * Count of visible items
	 * 
	 *  </br>
	 */
	private int visibleItems = DEF_VISIBLE_ITEMS;
	
	// Item height
	/**
	 * Item height
	 * 
	 *  </br>
	 */
	private int itemHeight = 0;

	// Text paints
	/**
	 * Text paints
	 * 
	 *  </br>
	 */
	private TextPaint itemsPaint;
	/**
	 * Text paints
	 * 
	 *  </br>
	 */
	private TextPaint valuePaint;

	// Layouts
	/**
	 * Layouts
	 * 
	 *  </br>
	 */
	private StaticLayout itemsLayout;
	/**
	 * Layouts
	 * 
	 *  </br>
	 */
	private StaticLayout labelLayout;
	/**
	 * Layouts
	 * 
	 *  </br>
	 * ѡ���� �� ����
	 */
	private StaticLayout valueLayout;

	// Label & background
	/**
	 * Label & background
	 * 
	 *  </br>
	 * ��ǩ
	 */
	private String label;
	/**
	 * Label & background
	 * �м�ļ����� </br>
	 * ѡ������ı���
	 */
	private Drawable centerDrawable;

	// Shadows drawables
	/**
	 * Shadows drawables
	 * 
	 *  </br>
	 * �ϱ� �� �ײ� ����Ӱ���ֵı�����Դ
	 */
	private GradientDrawable topShadow;
	private GradientDrawable bottomShadow;

	// Scrolling
	/**
	 * Scrolling
	 * 
	 *  </br>
	 * ִ�й�����
	 */
	private boolean isScrollingPerformed; 
	/**
	 * Scrolling
	 * 
	 *  </br>
	 * ����������
	 */
	private int scrollingOffset;

	// Scrolling animation
	/**
	 * Scrolling animation
	 * 
	 *  </br>
	 * ���Ƽ����
	 */
	private GestureDetector gestureDetector;
	/**
	 * Scrolling animation
	 * 
	 *  </br>
	 * ����
	 */
	private Scroller scroller;
	/**
	 * Scrolling animation
	 * 
	 *  </br>
	 * ���� ����Y
	 */
	private int lastScrollY;

	// Cyclic
	/**
	 * Cyclic
	 * 
	 *  </br>
	 * �Ƿ�ѭ��
	 */
	boolean isCyclic = false;
	
	// Listeners
	/**
	 * Listeners
	 * 
	 *  </br>
	 * �ؼ��ı�������� ����
	 */
	private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
	/**
	 * Listeners
	 * 
	 *  </br>
	 * �ؼ������������� ����
	 */
	private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();

	/**
	 * Constructor
	 *
	 * </br>
	 * ������ ��ʵ�������Ƽ���� �� ����
	 */
	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData(context);
	}

	/**
	 * Constructor
	 * 
	 * </br>
	 * ������ ��ʵ�������Ƽ���� �� ����
	 */
	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	/**
	 * Constructor
	 *
	 * </br>
	 * ������ ��ʵ�������Ƽ���� �� ����
	 */
	public WheelView(Context context) {
		super(context);
		initData(context);
	}
	
	/**
	 * Initializes class data
	 * @param context the context
	 *
	 * </br>
	 * ���ݳ�ʼ�� </br>
	 * ���ǰ����Ƽ���� �� ������ ʵ���� </br>
	 * ������������еĹ������ж���������
	 */
	private void initData(Context context) {
		gestureDetector = new GestureDetector(context, gestureListener);
		gestureDetector.setIsLongpressEnabled(false); //���û��������ʲô�ã������ã������ó�true����Ӱ��Ч��
		
		scroller = new Scroller(context);
	}
	
	/**
	 * Gets wheel adapter
	 * @return the adapter
	 *
	 * </br>
	 * ���������
	 */
	public WheelAdapter getAdapter() {
		return adapter;
	}
	
	/**
	 * Sets wheel adapter
	 * @param adapter the new wheel adapter
	 *
	 * </br>
	 * ���������� </br>
	 * �������������ػ�
	 */
	public void setAdapter(WheelAdapter adapter) {
		this.adapter = adapter;
		invalidateLayouts();
		invalidate();
	}
	
	/**
	 * Set the the specified scrolling interpolator
	 * @param interpolator the interpolator
	 *
	 * </br>
	 * ����Ҳ������ ����İɣ���ͨ�������岹����ʵ�� �������
	 */
	public void setInterpolator(Interpolator interpolator) {
		scroller.forceFinished(true);
		scroller = new Scroller(getContext(), interpolator);
	}
	
	/**
	 * Gets count of visible items
	 * 
	 * @return the count of visible items
	 *
	 * </br>
	 * ��ÿɼ��������
	 */
	public int getVisibleItems() {
		return visibleItems;
	}

	/**
	 * Sets count of visible items
	 * 
	 * @param count
	 *            the new count
	 *
	 * </br>
	 * ���ÿɼ�������� ���ػ�view
	 */
	public void setVisibleItems(int count) {
		visibleItems = count;
		invalidate();
	}

	/**
	 * Gets label
	 * 
	 * @return the label
	 *
	 * </br>
	 * ��ñ�ǩ
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets label
	 * 
	 * @param newLabel
	 *            the label to set
	 *
	 * </br>
	 * ���ñ�ǩ
	 */
	public void setLabel(String newLabel) {
		if (label == null || !label.equals(newLabel)) {
			label = newLabel;
			labelLayout = null;
			invalidate();
		}
	}
	
	/**
	 * Adds wheel changing listener
	 * @param listener the listener 
	 *
	 * </br>
	 * ��ӿؼ��ı������
	 */
	public void addChangingListener(OnWheelChangedListener listener) {
		changingListeners.add(listener);
	}

	/**
	 * Removes wheel changing listener
	 * @param listener the listener
	 *
	 * </br>
	 * �Ƴ��ؼ��ı������
	 */
	public void removeChangingListener(OnWheelChangedListener listener) {
		changingListeners.remove(listener);
	}
	
	/**
	 * Notifies changing listeners
	 * @param oldValue the old wheel value
	 * @param newValue the new wheel value
	 *
	 * </br>
	 * ֪ͨ �ı�������� </br>
	 */
	protected void notifyChangingListeners(int oldValue, int newValue) {
		for (OnWheelChangedListener listener : changingListeners) {
			listener.onChanged(this, oldValue, newValue);
		}
	}

	/**
	 * Adds wheel scrolling listener
	 * @param listener the listener 
	 *
	 * </br>
	 * ��ӿؼ�����������
	 */
	public void addScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.add(listener);
	}

	/**
	 * Removes wheel scrolling listener
	 * @param listener the listener
	 *
	 * </br>
	 * �Ƴ��ؼ�����������
	 */
	public void removeScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.remove(listener);
	}
	
	/**
	 * Notifies listeners about starting scrolling
	 *
	 * </br>
	 * ֪ͨ�ؼ��������������ÿ�ʼ�����ķ���
	 */
	protected void notifyScrollingListenersAboutStart() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingStarted(this);
		}
	}

	/**
	 * Notifies listeners about ending scrolling
	 *
	 * </br>
	 * ֪ͨ�ؼ��������������ý��������ķ���
	 */
	protected void notifyScrollingListenersAboutEnd() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingFinished(this);
		}
	}

	/**
	 * Gets current value
	 * 
	 * @return the current value
	 *
	 * </br>
	 * ���ص�ǰ�������
	 */
	public int getCurrentItem() {
		return currentItem;
	}

	/**
	 * Sets the current item. Does nothing when index is wrong.
	 * 
	 * @param index the item index
	 * @param animated the animation flag
	 *
	 * </br>
	 * ���õ�ǰ�� �������������������ؼ�ʲô��������Ӵ </br>
	 * �ڶ��������������Ƿ�ʹ�ù���������
	 */
	public void setCurrentItem(int index, boolean animated) {
		if (adapter == null || adapter.getItemsCount() == 0) {
			return; // throw?
		}
		if (index < 0 || index >= adapter.getItemsCount()) {
			if (isCyclic) {
				while (index < 0) {
					index += adapter.getItemsCount();
				}
				index %= adapter.getItemsCount();
			} else{
				return; // throw?
			}
		}
		if (index != currentItem) {
			if (animated) {
				scroll(index - currentItem, SCROLLING_DURATION);
			} else {
				invalidateLayouts();
			
				int old = currentItem;
				currentItem = index;
			
				notifyChangingListeners(old, currentItem);
			
				invalidate();
			}
		}
	}

	/**
	 * Sets the current item w/o animation. Does nothing when index is wrong.
	 * 
	 * @param index the item index
	 *
	 * </br>
	 * ���õ�ǰѡ���Ĭ���ǲ�����������
	 */
	public void setCurrentItem(int index) {
		setCurrentItem(index, false);
	}	
	
	/**
	 * Tests if wheel is cyclic. That means before the 1st item there is shown the last one
	 * @return true if wheel is cyclic
	 *
	 * </br>
	 * �Ƿ�ѭ����ʾ
	 */
	public boolean isCyclic() {
		return isCyclic;
	}

	/**
	 * Set wheel cyclic flag
	 * @param isCyclic the flag to set
	 *
	 * </br>
	 * �����Ƿ�ѭ����ʾ
	 */
	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;
		
		invalidate();
		invalidateLayouts();
	}

	/**
	 * Invalidates layouts
	 * 
	 *  </br>
	 * �ػ沼�� </br>
	 * ������ ѡ���itemsLayout �� ѡ����� valueLayout ��ֵΪnull </br>
	 * ͬ�½� ��������?scrollingOffset ����Ϊ0 </br>
	 * �����scrollingOffset ��û��������ʲô�ã�
	 */
	private void invalidateLayouts() {
		itemsLayout = null;
		valueLayout = null;
		scrollingOffset = 0;
	}

	/**
	 * Initializes resources
	 *
	 * </br>
	 * ��ʼ��Դ </br>
	 * ��������������ģ� �ж�ǰ�涨��Ļ��ʡ�������Դ��˽�����Ե�ֵ�������null�����´Ӿ�̬������ȡֵ����������Ӧ�����ԡ� </br>
	 * �����Щ��Ҫ����Ϊ�յĻ����������Ӧ�����˳�ʼ��������
	 */
	private void initResourcesIfNecessary() {
		if (itemsPaint == null) {
			itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
					| Paint.FAKE_BOLD_TEXT_FLAG);
			//itemsPaint.density = getResources().getDisplayMetrics().density;
			itemsPaint.setTextSize(TEXT_SIZE);
		}

		if (valuePaint == null) {
			valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
					| Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);
			//valuePaint.density = getResources().getDisplayMetrics().density;
			valuePaint.setTextSize(TEXT_SIZE);
			valuePaint.setShadowLayer(0.1f, 0, 0.1f, 0xFFC0C0C0);
		}

		if (centerDrawable == null) {
			centerDrawable = getContext().getResources().getDrawable(R.drawable.wheel_val);
		}

		if (topShadow == null) {
			topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
		}

		if (bottomShadow == null) {
			bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
		}

		setBackgroundResource(R.drawable.wheel_bg);
	}

	/**
	 * Calculates desired height for layout
	 * 
	 * @param layout
	 *            the source layout
	 * @return the desired layout height
	 * 
	 * </br>
	 * �������Ŀؼ��߶ȣ�����֤�䲻���ڽ������С�߶�
	 */
	private int getDesiredHeight(Layout layout) {
		if (layout == null) {
			return 0;
		}

		int desired = getItemHeight() * visibleItems - ITEM_OFFSET * 2
				- ADDITIONAL_ITEM_HEIGHT;

		// Check against our minimum height
		desired = Math.max(desired, getSuggestedMinimumHeight());

		return desired;
	}

	/**
	 * Returns text item by index
	 * @param index the item index
	 * @return the item or null
	 * 
	 * </br> 
	 * ָ�����������ѡ����ı�ֵ(Sring) </br>
	 * �������������Χ���ؼ��ֲ���ѭ���ģ�isCyclic�����򷵻�null </br>
	 * �����ѭ��������������ڲ������˴��� </br>
	 * Ϊ������+count, Ȼ��ȡ��
	 */
	private String getTextItem(int index) {
		if (adapter == null || adapter.getItemsCount() == 0) {
			return null;
		}
		int count = adapter.getItemsCount();
		if ((index < 0 || index >= count) && !isCyclic) {
			return null;
		} else {
			while (index < 0) {
				index = count + index;
			}
		}
		
		index %= count;
		return adapter.getItem(index);
	}
	
	/**
	 * Builds text depending on current value
	 * 
	 * @param useCurrentValue
	 * @return the text
	 * 
	 *  </br>
	 * ������ǰ�� �����ı� </br>
	 * ���һ���ַ����������ǰ��Ŀ������Ϊ5, �ɼ���Ŀ��Ϊ3  </br>
	 * ���ַ�����ֵΪ getTextItem(3).append("\n").getTextItem(4).append("\n").getTextItem(5).append("\n").getTextItem(6).append("\n").getTextItem(7) </br>
	 * ��� ����useCurrentValueΪfalse </br>
	 * �򷵻ص��ַ���Ϊ getTextItem(3).append("\n").getTextItem(4).append("\n").getTextItem(6).append("\n").getTextItem(7) </br>
	 * ���getTextItem(i)����null, �򲻻��򷵻�ֵ��׷��
	 */
	private String buildText(boolean useCurrentValue) {
		StringBuilder itemsText = new StringBuilder();
		int addItems = visibleItems / 2 + 1;

		for (int i = currentItem - addItems; i <= currentItem + addItems; i++) {
			if (useCurrentValue || i != currentItem) {
				String text = getTextItem(i);
				if (text != null) {
					itemsText.append(text);
				}
			}
			if (i < currentItem + addItems) {
				itemsText.append("\n");
			}
		}
		
		return itemsText.toString();
	}

	/**
	 * Returns the max item length that can be present
	 * @return the max length
	 * 
	 *  </br>
	 *  ��������ı�����</br>
	 *  �������ؼ�����õ�
	 */
	private int getMaxTextLength() {
		WheelAdapter adapter = getAdapter();
		if (adapter == null) {
			return 0;
		}
		
		int adapterLength = adapter.getMaximumLength();
		if (adapterLength > 0) {
			return adapterLength;
		}

		String maxText = null;
		int addItems = visibleItems / 2;
		for (int i = Math.max(currentItem - addItems, 0);
				i < Math.min(currentItem + visibleItems, adapter.getItemsCount()); i++) {
			String text = adapter.getItem(i);
			if (text != null && (maxText == null || maxText.length() < text.length())) {
				maxText = text;
			}
		}//���ѭ���ķ�Χû������ѽ����ʼֵ������ѭ���� ����Ϊʲô�� ��ǰ������+�ɼ���Ŀ����

		return maxText != null ? maxText.length() : 0;
	}

	/**
	 * Returns height of wheel item
	 * @return the item height
	 * 
	 * </br>
	 * ���ѡ���
	 */
	private int getItemHeight() {
		if (itemHeight != 0) {
			return itemHeight;
		} else if (itemsLayout != null && itemsLayout.getLineCount() > 2) {
			itemHeight = itemsLayout.getLineTop(2) - itemsLayout.getLineTop(1);
			return itemHeight;
		}//�����itemlayout ΪʲôҪ�� �����е�top����һ�е�top�أ�����Ӧ�÷���layout�ĸ��û������
		
		return getHeight() / visibleItems;
	}

	/**
	 * Calculates control width and creates text layouts
	 * @param widthSize the input layout width
	 * @param mode the layout mode
	 * @return the calculated control width
	 * 
	 * </br>
	 * ���㲼�ֿ�
	 */
	private int calculateLayoutWidth(int widthSize, int mode) {
		initResourcesIfNecessary();

		int width = widthSize;

		int maxLength = getMaxTextLength();
		if (maxLength > 0) {
			float textWidth = FloatMath.ceil(Layout.getDesiredWidth("0", itemsPaint));
			itemsWidth = (int) (maxLength * textWidth);
		} else {
			itemsWidth = 0;
		}
		itemsWidth += ADDITIONAL_ITEMS_SPACE; // make it some more

		labelWidth = 0;
		if (label != null && label.length() > 0) {
			labelWidth = (int) FloatMath.ceil(Layout.getDesiredWidth(label, valuePaint));
		}

		boolean recalculate = false;
		if (mode == MeasureSpec.EXACTLY) {
			width = widthSize;
			recalculate = true;
		} else {
			width = itemsWidth + labelWidth + 2 * PADDING;
			if (labelWidth > 0) {
				width += LABEL_OFFSET;
			}

			// Check against our minimum width
			width = Math.max(width, getSuggestedMinimumWidth());

			if (mode == MeasureSpec.AT_MOST && widthSize < width) {
				width = widthSize;
				recalculate = true;
			}
		}

		if (recalculate) {
			// recalculate width
			int pureWidth = width - LABEL_OFFSET - 2 * PADDING;
			if (pureWidth <= 0) {
				itemsWidth = labelWidth = 0;
			}
			if (labelWidth > 0) {
				double newWidthItems = (double) itemsWidth * pureWidth
						/ (itemsWidth + labelWidth);
				itemsWidth = (int) newWidthItems;
				labelWidth = pureWidth - itemsWidth;
			} else {
				itemsWidth = pureWidth + LABEL_OFFSET; // no label
			}
		}

		if (itemsWidth > 0) {
			createLayouts(itemsWidth, labelWidth);
		}

		return width;
	}

	/**
	 * Creates layouts
	 * @param widthItems width of items layout
	 * @param widthLabel width of label layout
	 * 
	 * </br>
	 * ��������</br>
	 */
	private void createLayouts(int widthItems, int widthLabel) {
		if (itemsLayout == null || itemsLayout.getWidth() > widthItems) {
			itemsLayout = new StaticLayout(buildText(isScrollingPerformed), itemsPaint, widthItems,
					widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_CENTER,
					1, ADDITIONAL_ITEM_HEIGHT, false);
		} else {
			itemsLayout.increaseWidthTo(widthItems);
		}

		if (!isScrollingPerformed && (valueLayout == null || valueLayout.getWidth() > widthItems)) {
			String text = getAdapter() != null ? getAdapter().getItem(currentItem) : null;
			valueLayout = new StaticLayout(text != null ? text : "",
					valuePaint, widthItems, widthLabel > 0 ?
							Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_CENTER,
							1, ADDITIONAL_ITEM_HEIGHT, false);
		} else if (isScrollingPerformed) {
			valueLayout = null;
		} else {
			valueLayout.increaseWidthTo(widthItems);
		}

		if (widthLabel > 0) {
			if (labelLayout == null || labelLayout.getWidth() > widthLabel) {
				labelLayout = new StaticLayout(label, valuePaint,
						widthLabel, Layout.Alignment.ALIGN_NORMAL, 1,
						ADDITIONAL_ITEM_HEIGHT, false);
			} else {
				labelLayout.increaseWidthTo(widthLabel);
			}
		}
	}

	/**
	 * ��дonMeasure ���óߴ�
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width = calculateLayoutWidth(widthSize, widthMode);

		int height;
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = getDesiredHeight(itemsLayout);

			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}

		setMeasuredDimension(width, height);
	}

	/**
	 * ����
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (itemsLayout == null) {
			if (itemsWidth == 0) {
				calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
			} else {
				createLayouts(itemsWidth, labelWidth);
			}
		}

		if (itemsWidth > 0) {
			canvas.save();
			// Skip padding space and hide a part of top and bottom items
			canvas.translate(PADDING, -ITEM_OFFSET);
			drawItems(canvas);
			drawValue(canvas);
			canvas.restore();
		}

		drawCenterRect(canvas);
		drawShadows(canvas);
	}

	/**
	 * Draws shadows on top and bottom of control
	 * @param canvas the canvas for drawing
	 * 
	 * </br>
	 * ���ƿؼ����� �͵ײ��� ��Ӱ���� </br>
	 * ��Ҫ���뻭������
	 */
	private void drawShadows(Canvas canvas) {
		topShadow.setBounds(0, 0, getWidth(), getHeight() / visibleItems);
		topShadow.draw(canvas);

		bottomShadow.setBounds(0, getHeight() - getHeight() / visibleItems,
				getWidth(), getHeight());
		bottomShadow.draw(canvas);
	}

	/**
	 * Draws value and label layout
	 * @param canvas the canvas for drawing
	 * 
	 * </br>
	 * ����ѡ���� �� ��ǩ
	 */
	private void drawValue(Canvas canvas) {
		valuePaint.setColor(VALUE_TEXT_COLOR);
		valuePaint.drawableState = getDrawableState();

		Rect bounds = new Rect();
		itemsLayout.getLineBounds(visibleItems / 2, bounds);

		// draw label
		if (labelLayout != null) {
			canvas.save();
			canvas.translate(itemsLayout.getWidth() + LABEL_OFFSET, bounds.top);
			labelLayout.draw(canvas);
			canvas.restore();
		}

		// draw current value
		if (valueLayout != null) {
			canvas.save();
			canvas.translate(0, bounds.top + scrollingOffset);
			valueLayout.draw(canvas);
			canvas.restore();
		}
	}

	/**
	 * Draws items
	 * @param canvas the canvas for drawing
	 * 
	 * </br>
	 * ����ѡ��
	 */
	private void drawItems(Canvas canvas) {
		canvas.save();
		
		int top = itemsLayout.getLineTop(1);
		canvas.translate(0, - top + scrollingOffset);
		
		itemsPaint.setColor(ITEMS_TEXT_COLOR);
		itemsPaint.drawableState = getDrawableState();
		itemsLayout.draw(canvas);
		
		canvas.restore();
	}

	/**
	 * Draws rect for current value
	 * @param canvas the canvas for drawing
	 * 
	 * </br>
	 * �����м�ľ�������
	 */
	private void drawCenterRect(Canvas canvas) {
		int center = getHeight() / 2;
		int offset = getItemHeight() / 2;
		centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
		centerDrawable.draw(canvas);
	}

	/**
	 * �����¼��� �ص�����</br>
	 * �����˰� ��������� adapter��null �Ļ���������ʲô���������ġ�</br>
	 * �����������Ϊ�գ���MotionEvent���ݸ�����ʶ������ ���ж��Ƿ���ACTION_UP </br>
	 * �����˵�������ѽ��� ����justify()������</br>
	 * return true. ����й¶
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		WheelAdapter adapter = getAdapter();
		if (adapter == null) {
			return true;
		}
		
			if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
			justify();
		}
		return true;
	}
	
	/**
	 * Scrolls the wheel
	 * @param delta the scrolling value
	 * 
	 * </br>
	 * ����</br>
	 * ����ֻ�����¶����� scrollingOffset��ֵ��</br>
	 * ִ�й����Ĳ�����������</br>
	 * �����󿴰ɡ�
	 */
	private void doScroll(int delta) {
		scrollingOffset += delta;
		
		int count = scrollingOffset / getItemHeight();
		int pos = currentItem - count;
		if (isCyclic && adapter.getItemsCount() > 0) {
			// fix position by rotating
			while (pos < 0) {
				pos += adapter.getItemsCount();
			}
			pos %= adapter.getItemsCount();
		} else if (isScrollingPerformed) {
			// 
			if (pos < 0) {
				count = currentItem;
				pos = 0;
			} else if (pos >= adapter.getItemsCount()) {
				count = currentItem - adapter.getItemsCount() + 1;
				pos = adapter.getItemsCount() - 1;
			}
		} else {
			// fix position
			pos = Math.max(pos, 0);
			pos = Math.min(pos, adapter.getItemsCount() - 1);
		}
		
		int offset = scrollingOffset;
		if (pos != currentItem) {
			setCurrentItem(pos, false);
		} else {
			invalidate();
		}
		
		// update offset
		scrollingOffset = offset - count * getItemHeight();
		if (scrollingOffset > getHeight()) {
			scrollingOffset = scrollingOffset % getHeight() + getHeight();
		}
	}
	
	// gesture listener
	/**
	 * gesture listener
	 * 
	 * </br>
	 * ���Ƽ�����</br>
	 * ��������Ӧ�û�fling �Լ� scroll�����Ĵ���
	 */
	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		/**
		 * ����ǰ���ʱֹͣ�����Ĳ���
		 */
		public boolean onDown(MotionEvent e) {
			if (isScrollingPerformed) {
				scroller.forceFinished(true);
				clearMessages();
				return true;
			}
			return false;
		}
		
		/**
		 * scroll
		 */
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			startScrolling();
			doScroll((int)-distanceY);
			return true;
		}
		
		/**
		 * fling
		 */
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			lastScrollY = currentItem * getItemHeight() + scrollingOffset;
			int maxY = isCyclic ? 0x7FFFFFFF : adapter.getItemsCount() * getItemHeight();
			int minY = isCyclic ? -maxY : 0;
			scroller.fling(0, lastScrollY, 0, (int) -velocityY / 2, 0, 0, minY, maxY);
			setNextMessage(MESSAGE_SCROLL);
			return true;
		}
	};

	// Messages
	/**
	 * Messages
	 * 
	 * </br>
	 * �򶯻����������͵���Ϣ -����
	 */
	private final int MESSAGE_SCROLL = 0;
	/**
	 * Messages
	 * 
	 * </br>
	 * �򶯻����������͵���Ϣ -֤��
	 */
	private final int MESSAGE_JUSTIFY = 1;
	
	/**
	 * Set next message to queue. Clears queue before.
	 * 
	 * @param message the message to set
	 * 
	 * </br>
	 * �������������animationHandler�е�ԭ����Ϣ������������Ϣ
	 */
	private void setNextMessage(int message) {
		clearMessages();
		animationHandler.sendEmptyMessage(message);
	}

	/**
	 * Clears messages from queue
	 * 
	 * </br>
	 * ���������������ԭ�е���Ϣ
	 */
	private void clearMessages() {
		animationHandler.removeMessages(MESSAGE_SCROLL);
		animationHandler.removeMessages(MESSAGE_JUSTIFY);
	}
	
	// animation handler
	/**
	 * animation handler
	 * 
	 * </br>
	 * ����������
	 */
	private Handler animationHandler = new Handler() {
		public void handleMessage(Message msg) {
			scroller.computeScrollOffset();
			int currY = scroller.getCurrY();
			int delta = lastScrollY - currY;
			lastScrollY = currY;
			if (delta != 0) {
				doScroll(delta);
			}
			
			// scrolling is not finished when it comes to final Y
			// so, finish it manually 
			if (Math.abs(currY - scroller.getFinalY()) < MIN_DELTA_FOR_SCROLLING) {
				currY = scroller.getFinalY();
				scroller.forceFinished(true);
			}
			if (!scroller.isFinished()) {
				animationHandler.sendEmptyMessage(msg.what);
			} else if (msg.what == MESSAGE_SCROLL) {
				justify();
			} else {
				finishScrolling();
			}
		}
	};
	
	/**
	 * Justifies wheel
	 * 
	 * </br>
	 * ��֤����
	 */
	private void justify() {
		if (adapter == null) {
			return;
		}
		
		lastScrollY = 0;
		int offset = scrollingOffset;
		int itemHeight = getItemHeight();
		boolean needToIncrease = offset > 0 ? currentItem < adapter.getItemsCount() : currentItem > 0; 
		if ((isCyclic || needToIncrease) && Math.abs((float) offset) > (float) itemHeight / 2) {
			if (offset < 0)
				offset += itemHeight + MIN_DELTA_FOR_SCROLLING;
			else
				offset -= itemHeight + MIN_DELTA_FOR_SCROLLING;
		}
		if (Math.abs(offset) > MIN_DELTA_FOR_SCROLLING) {
			scroller.startScroll(0, 0, 0, offset, SCROLLING_DURATION);
			setNextMessage(MESSAGE_JUSTIFY);
		} else {
			finishScrolling();
		}
	}
	
	/**
	 * Starts scrolling
	 * 
	 * </br>
	 * ��ʼ����</br>
	 * ��֪ͨ��ʼ����������
	 */
	private void startScrolling() {
		if (!isScrollingPerformed) {
			isScrollingPerformed = true;
			notifyScrollingListenersAboutStart();
		}
	}

	/**
	 * Finishes scrolling
	 * 
	 * </br>
	 * ��������</br>
	 * ��֪ͨ��������������
	 */
	void finishScrolling() {
		if (isScrollingPerformed) {
			notifyScrollingListenersAboutEnd();
			isScrollingPerformed = false;
		}
		invalidateLayouts();
		invalidate();
	}
	
	
	/**
	 * Scroll the wheel
	 * @param itemsToSkip items to scroll
	 * @param time scrolling duration
	 * 
	 * ����
	 */
	public void scroll(int itemsToScroll, int time) {
		scroller.forceFinished(true);

		lastScrollY = scrollingOffset;
		int offset = itemsToScroll * getItemHeight();
		
		scroller.startScroll(0, lastScrollY, 0, offset - lastScrollY, time);
		setNextMessage(MESSAGE_SCROLL);
		
		startScrolling();
	}

}
