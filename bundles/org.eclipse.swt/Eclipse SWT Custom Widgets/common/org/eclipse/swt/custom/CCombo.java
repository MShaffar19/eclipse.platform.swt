/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.custom;


import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.accessibility.*;

/**
 * The CCombo class represents a selectable user interface object
 * that combines a text field and a list and issues notificiation
 * when an item is selected from the list.
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add children to it, or set a layout on it.
 * </p>
 * <dl>
 * <dt><b>Styles:</b>
 * <dd>BORDER, READ_ONLY, FLAT</dd>
 * <dt><b>Events:</b>
 * <dd>Selection</dd>
 * </dl>
 */
public final class CCombo extends Composite {

	static final int ITEMS_SHOWING = 5;

	Text text;
	List list;
	Shell popup;
	Button arrow;
	boolean hasFocus;
	
/**
 * Constructs a new instance of this class given its parent
 * and a style value describing its behavior and appearance.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together 
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p>
 *
 * @param parent a widget which will be the parent of the new instance (cannot be null)
 * @param style the style of widget to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 * </ul>
 *
 * @see SWT#BORDER
 * @see SWT#READ_ONLY
 * @see SWT#FLAT
 * @see Widget#getStyle
 */
public CCombo (Composite parent, int style) {
	super (parent, checkStyle (style));
	
	style = getStyle();
	
	int textStyle = SWT.SINGLE;
	if ((style & SWT.READ_ONLY) != 0) textStyle |= SWT.READ_ONLY;
	if ((style & SWT.FLAT) != 0) textStyle |= SWT.FLAT;
	text = new Text (this, textStyle);
	
	popup = new Shell (getShell (), SWT.NO_TRIM);
	
	int listStyle = SWT.SINGLE | SWT.V_SCROLL;
	if ((style & SWT.FLAT) != 0) listStyle |= SWT.FLAT;
	if ((style & SWT.RIGHT_TO_LEFT) != 0) listStyle |= SWT.RIGHT_TO_LEFT;
	if ((style & SWT.LEFT_TO_RIGHT) != 0) listStyle |= SWT.LEFT_TO_RIGHT;
	list = new List (popup, listStyle);
	
	int arrowStyle = SWT.ARROW | SWT.DOWN;
	if ((style & SWT.FLAT) != 0) arrowStyle |= SWT.FLAT;
	arrow = new Button (this, arrowStyle);

	Listener listener = new Listener () {
		public void handleEvent (Event event) {
			if (popup == event.widget) {
				popupEvent (event);
				return;
			}
			if (text == event.widget) {
				textEvent (event);
				return;
			}
			if (list == event.widget) {
				listEvent (event);
				return;
			}
			if (arrow == event.widget) {
				arrowEvent (event);
				return;
			}
			if (CCombo.this == event.widget) {
				comboEvent (event);
				return;
			}

		}
	};
	
	int [] comboEvents = {SWT.Dispose, SWT.Move, SWT.Resize};
	for (int i=0; i<comboEvents.length; i++) this.addListener (comboEvents [i], listener);
	
	int [] popupEvents = {SWT.Close, SWT.Paint, SWT.Deactivate};
	for (int i=0; i<popupEvents.length; i++) popup.addListener (popupEvents [i], listener);
	
	int [] textEvents = {SWT.KeyDown, SWT.KeyUp, SWT.Modify, SWT.MouseDown, SWT.MouseUp, SWT.Traverse, SWT.FocusIn, SWT.FocusOut};
	for (int i=0; i<textEvents.length; i++) text.addListener (textEvents [i], listener);
	
	int [] listEvents = {SWT.MouseUp, SWT.Selection, SWT.Traverse, SWT.KeyDown, SWT.KeyUp, SWT.FocusIn, SWT.FocusOut};
	for (int i=0; i<listEvents.length; i++) list.addListener (listEvents [i], listener);
	
	int [] arrowEvents = {SWT.Selection, SWT.FocusIn, SWT.FocusOut};
	for (int i=0; i<arrowEvents.length; i++) arrow.addListener (arrowEvents [i], listener);
	
	initAccessible();
}
static int checkStyle (int style) {
	int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
	return style & mask;
}
/**
* Adds an item.
* <p>
* The item is placed at the end of the list.
* Indexing is zero based.
*
* @param string the new item
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when the string is null
* @exception SWTError(ERROR_ITEM_NOT_ADDED)
*	when the item cannot be added
*/
public void add (String string) {
	checkWidget();
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	list.add (string);
}
/**
* Adds an item at an index.
* <p>
* The item is placed at an index in the list.
* Indexing is zero based.
*
* This operation will fail when the index is
* out of range.
*
* @param string the new item
* @param index the index for the item
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when the string is null
* @exception SWTError(ERROR_ITEM_NOT_ADDED)
*	when the item cannot be added
*/
public void add (String string, int index) {
	checkWidget();
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	list.add (string, index);
}
/**	 
* Adds the listener to receive events.
* <p>
*
* @param listener the listener
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when listener is null
*/
public void addModifyListener (ModifyListener listener) {;
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Modify, typedListener);
}
/**	 
* Adds the listener to receive events.
* <p>
*
* @param listener the listener
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when listener is null
*/
public void addSelectionListener(SelectionListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Selection,typedListener);
	addListener (SWT.DefaultSelection,typedListener);
}
void arrowEvent (Event event) {
	switch (event.type) {
		case SWT.FocusIn: {
			if (hasFocus) return;
			hasFocus = true;
			if (getEditable ()) text.selectAll ();
			Event e = new Event();
			e.time = event.time;
			notifyListeners(SWT.FocusIn, e);
			break;
		}
		case SWT.FocusOut: {
			event.display.asyncExec(new Runnable() {
				public void run() {
					if (CCombo.this.isDisposed()) return;
					Control focusControl = getDisplay().getFocusControl();
					if (focusControl == list || focusControl == text) return;
					hasFocus = false;
					Event e = new Event();
					notifyListeners(SWT.FocusOut, e);
				}
			});
			break;
		}
		case SWT.Selection: {
			dropDown (!isDropped ());
			break;
		}
	}
}
/**
* Clears the current selection.
* <p>
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
*/
public void clearSelection () {
	checkWidget();
	text.clearSelection ();
	list.deselectAll ();
}
void comboEvent (Event event) {
	switch (event.type) {
		case SWT.Dispose:
			if (popup != null && !popup.isDisposed ()) popup.dispose ();
			popup = null;  
			text = null;  
			list = null;  
			arrow = null;
			break;
		case SWT.Move:
			dropDown(false);
			break;
		case SWT.Resize:
			internalLayout();
			break;
	}
}

public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget();
	int width = 0, height = 0;
	Point textSize = text.computeSize (wHint, SWT.DEFAULT, changed);
	Point arrowSize = arrow.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
	Point listSize = list.computeSize (wHint, SWT.DEFAULT, changed);
	int borderWidth = getBorderWidth();
	
	height = Math.max (hHint, Math.max(textSize.y, arrowSize.y)  + 2*borderWidth);
	width = Math.max (wHint, Math.max(textSize.x + arrowSize.x + 2*borderWidth, listSize.x + 2)  );
	return new Point (width, height);
}
/**
* Deselects an item.
* <p>
* If the item at an index is selected, it is
* deselected.  If the item at an index is not
* selected, it remains deselected.  Indices
* that are out of range are ignored.  Indexing
* is zero based.
*
* @param index the index of the item
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
*/
public void deselect (int index) {
	checkWidget();
	list.deselect (index);
}
/**
* Deselects all items.
* <p>
*
* If an item is selected, it is deselected.
* If an item is not selected, it remains unselected.
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
*/
public void deselectAll () {
	checkWidget();
	list.deselectAll ();
}
void dropDown (boolean drop) {
	if (drop == isDropped ()) return;
	if (!drop) {
		popup.setVisible (false);
		text.setFocus();
		return;
	}

	int index = list.getSelectionIndex ();
	if (index != -1) list.setTopIndex (index);
	Rectangle listRect = list.getBounds ();
	Display display = getDisplay ();
	Rectangle rect = display.map (getParent (), null, getBounds ());
	Point comboSize = getSize ();
	int width = Math.max (comboSize.x, listRect.width + 2);
	popup.setBounds (rect.x, rect.y + comboSize.y, width, listRect.height + 2);
	popup.setVisible (true);
	list.setFocus ();
}
public Control [] getChildren () {
	checkWidget();
	return new Control [0];
}
boolean getEditable () {
	return text.getEditable ();
}
/**
* Gets an item at an index.
* <p>
* Indexing is zero based.
*
* This operation will fail when the index is out
* of range or an item could not be queried from
* the OS.
*
* @param index the index of the item
* @return the item
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_CANNOT_GET_ITEM)
*	when the operation fails
*/
public String getItem (int index) {
	checkWidget();
	return list.getItem (index);
}
/**
* Gets the number of items.
* <p>
* This operation will fail if the number of
* items could not be queried from the OS.
*
* @return the number of items in the widget
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_CANNOT_GET_COUNT)
*	when the operation fails
*/
public int getItemCount () {
	checkWidget();
	return list.getItemCount ();
}
/**
* Gets the height of one item.
* <p>
* This operation will fail if the height of
* one item could not be queried from the OS.
*
* @return the height of one item in the widget
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_CANNOT_GET_ITEM_HEIGHT)
*	when the operation fails
*/
public int getItemHeight () {
	checkWidget();
	return list.getItemHeight ();
}
/**
* Gets the items.
* <p>
* This operation will fail if the items cannot
* be queried from the OS.
*
* @return the items in the widget
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_CANNOT_GET_ITEM)
*	when the operation fails
*/
public String [] getItems () {
	checkWidget();
	return list.getItems ();
}
/**
* Gets the selection.
* <p>
* @return a point representing the selection start and end
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
*/
public Point getSelection () {
	checkWidget();
	return text.getSelection ();
}
/**
* Gets the index of the selected item.
* <p>
* Indexing is zero based.
* If no item is selected -1 is returned.
*
* @return the index of the selected item.
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
*/
public int getSelectionIndex () {
	checkWidget();
	return list.getSelectionIndex ();
}
/**
* Gets the widget text.
* <p>
* If the widget has no text, an empty string is returned.
*
* @return the widget text
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
*/
public String getText () {
	checkWidget();
	return text.getText ();
}
/**
* Gets the height of the combo's text field.
* <p>
* The operation will fail if the height cannot 
* be queried from the OS.

* @return the height of the combo's text field.
* 
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_ERROR_CANNOT_GET_ITEM_HEIGHT)
*	when the operation fails
*/
public int getTextHeight () {
	checkWidget();
	return text.getLineHeight();
}
/**
* Gets the text limit.
* <p>
* @return the text limit
* 
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
*/
public int getTextLimit () {
	checkWidget();
	return text.getTextLimit ();
}
/**
* Gets the index of an item.
* <p>
* The list is searched starting at 0 until an
* item is found that is equal to the search item.
* If no item is found, -1 is returned.  Indexing
* is zero based.
*
* @param string the search item
* @return the index of the item
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when string is null
*/
public int indexOf (String string) {
	checkWidget();
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	return list.indexOf (string);
}
/**
* Gets the index of an item.
* <p>
* The widget is searched starting at start including
* the end position until an item is found that
* is equal to the search itenm.  If no item is
* found, -1 is returned.  Indexing is zero based.
*
* @param string the search item
* @param index the starting position
* @return the index of the item
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when string is null
*/
public int indexOf (String string, int start) {
	checkWidget();
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	return list.indexOf (string, start);
}

void initAccessible() {
	getAccessible().addAccessibleListener(new AccessibleAdapter() {
		public void getHelp(AccessibleEvent e) {
			e.result = getToolTipText();
		}
	});
		
	getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
		public void getChildAtPoint(AccessibleControlEvent e) {
			Point testPoint = toControl(new Point(e.x, e.y));
			if (getBounds().contains(testPoint)) {
				e.childID = ACC.CHILDID_SELF;
			}
		}
		
		public void getLocation(AccessibleControlEvent e) {
			Rectangle location = getBounds();
			Point pt = toDisplay(new Point(location.x, location.y));
			e.x = pt.x;
			e.y = pt.y;
			e.width = location.width;
			e.height = location.height;
		}
		
		public void getChildCount(AccessibleControlEvent e) {
			e.detail = 0;
		}
		
		public void getRole(AccessibleControlEvent e) {
			e.detail = ACC.ROLE_COMBOBOX;
		}
		
		public void getState(AccessibleControlEvent e) {
			e.detail = ACC.STATE_NORMAL;
		}

		public void getValue(AccessibleControlEvent e) {
			e.result = getText();
		}
	});
}
boolean isDropped () {
	return popup.getVisible ();
}
public boolean isFocusControl () {
	checkWidget();
	if (text.isFocusControl() || arrow.isFocusControl() || list.isFocusControl() || popup.isFocusControl()) {
		return true;
	} else {
		return super.isFocusControl();
	}
}
void internalLayout () {
	if (isDropped ()) dropDown (false);
	
	Rectangle rect = getClientArea();
	int width = rect.width;
	int height = rect.height;
	Point arrowSize = arrow.computeSize(SWT.DEFAULT, height);
	text.setBounds (0, 0, width - arrowSize.x, height);
	arrow.setBounds (width - arrowSize.x, 0, arrowSize.x, arrowSize.y);
	
	Point size = getSize();
	int itemHeight = list.getItemHeight () * ITEMS_SHOWING;
	Point listSize = list.computeSize (SWT.DEFAULT, itemHeight);
	list.setBounds (1, 1, Math.max (size.x - 2, listSize.x), listSize.y);
}
void listEvent (Event event) {
	switch (event.type) {
		case SWT.FocusIn: {
			if (hasFocus) return;
			hasFocus = true;
			if (getEditable ()) text.selectAll ();
			Event e = new Event();
			e.time = event.time;
			notifyListeners(SWT.FocusIn, e);
			break;
		}
		case SWT.FocusOut: {
			event.display.asyncExec(new Runnable() {
				public void run() {
					if (CCombo.this.isDisposed()) return;
					Control focusControl = getDisplay().getFocusControl();
					if (focusControl == text || focusControl == arrow) return;
					hasFocus = false;
					Event e = new Event();
					notifyListeners(SWT.FocusOut, e);
				}
			});
			break;
		}
		case SWT.MouseUp: {
			if (event.button != 1) return;
			dropDown (false);
			Event e = new Event();
			e.time = event.time;
			notifyListeners(SWT.DefaultSelection, e);
			break;
		}
		case SWT.Selection: {
			int index = list.getSelectionIndex ();
			if (index == -1) return;
			text.setText (list.getItem (index));
			text.selectAll ();
			list.setSelection(index);
			Event e = new Event();
			e.time = event.time;
			e.stateMask = event.stateMask;
			e.doit = event.doit;
			notifyListeners(SWT.Selection, e);
			event.doit = e.doit;
			break;
		}
		case SWT.Traverse: {
			switch (event.detail) {
				case SWT.TRAVERSE_TAB_NEXT:
				case SWT.TRAVERSE_RETURN:
				case SWT.TRAVERSE_ESCAPE:
				case SWT.TRAVERSE_ARROW_PREVIOUS:
				case SWT.TRAVERSE_ARROW_NEXT:
					event.doit = false;
					break;
			}
			Event e = new Event();
			e.time = event.time;
			e.detail = event.detail;
			e.doit = event.doit;
			e.keyCode = event.keyCode;
			notifyListeners(SWT.Traverse, e);
			event.doit = e.doit;
			break;
		}
		case SWT.KeyUp: {		
			Event e = new Event();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners(SWT.KeyUp, e);
			break;
		}
		case SWT.KeyDown: {
			if (event.character == SWT.ESC) { 
				// escape key cancels popup list
				dropDown (false);
			}
			if (event.character == SWT.CR || event.character == '\t') {
				// Enter and Tab cause default selection
				dropDown (false);
				Event e = new Event();
				e.time = event.time;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.DefaultSelection, e);
			}
			//At this point the widget may have been disposed.
			// If so, do not continue.
			if (isDisposed()) break;
			Event e = new Event();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners(SWT.KeyDown, e);
			break;
			
		}
	}
}
void popupEvent(Event event) {
	switch (event.type) {
		case SWT.Paint:
			// draw black rectangle around list
			Rectangle listRect = list.getBounds();
			Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
			event.gc.setForeground(black);
			event.gc.drawRectangle(0, 0, listRect.width + 1, listRect.height + 1);
			break;
		case SWT.Close:
			event.doit = false;
			dropDown (false);
			break;
		case SWT.Deactivate:
			dropDown (false);
			break;
	}
}
public void redraw (int x, int y, int width, int height, boolean all) {
	checkWidget();
	if (!all) return;
	Point location = text.getLocation();
	text.redraw(x - location.x, y - location.y, width, height, all);
	location = list.getLocation();
	list.redraw(x - location.x, y - location.y, width, height, all);
	if (arrow != null) {
		location = arrow.getLocation();
		arrow.redraw(x - location.x, y - location.y, width, height, all);
	}
}

/**
* Removes an item at an index.
* <p>
* Indexing is zero based.
*
* This operation will fail when the index is out
* of range or an item could not be removed from
* the OS.
*
* @param index the index of the item
* @return the selection state
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_ITEM_NOT_REMOVED)
*	when the operation fails
*/
public void remove (int index) {
	checkWidget();
	list.remove (index);
}
/**
* Removes a range of items.
* <p>
* Indexing is zero based.  The range of items
* is from the start index up to and including
* the end index.
*
* This operation will fail when the index is out
* of range or an item could not be removed from
* the OS.
*
* @param start the start of the range
* @param end the end of the range
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_ITEM_NOT_REMOVED)
*	when the operation fails
*/
public void remove (int start, int end) {
	checkWidget();
	list.remove (start, end);
}
/**
* Removes an item.
* <p>
* This operation will fail when the item
* could not be removed from the OS.
*
* @param string the search item
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when string is null
* @exception SWTError(ERROR_ITEM_NOT_REMOVED)
*	when the operation fails
*/
public void remove (String string) {
	checkWidget();
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	list.remove (string);
}
/**
* Removes all items.
* <p>
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
*/
public void removeAll () {
	checkWidget();
	text.setText (""); //$NON-NLS-1$
	list.removeAll ();
}
/**	 
* Removes the listener.
* <p>
*
* @param listener the listener
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when listener is null
*/
public void removeModifyListener (ModifyListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	removeListener(SWT.Modify, listener);	
}
/**	 
* Removes the listener.
* <p>
*
* @param listener the listener
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when listener is null
*/
public void removeSelectionListener (SelectionListener listener) {
	checkWidget();
	if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	removeListener(SWT.Selection, listener);
	removeListener(SWT.DefaultSelection,listener);	
}
/**
* Selects an item.
* <p>
* If the item at an index is not selected, it is
* selected. Indices that are out of
* range are ignored.  Indexing is zero based.
*
* @param index the index of the item
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
*/
public void select (int index) {
	checkWidget();
	if (index == -1) {
		list.deselectAll ();
		text.setText (""); //$NON-NLS-1$
		return;
	}
	if (0 <= index && index < list.getItemCount()) {
		if (index != getSelectionIndex()) {
			text.setText (list.getItem (index));
			text.selectAll ();
			list.select (index);
			list.showSelection ();
		}
	}
}
public void setBackground (Color color) {
	super.setBackground(color);
	if (text != null) text.setBackground(color);
	if (list != null) list.setBackground(color);
	if (arrow != null) arrow.setBackground(color);
}
public boolean setFocus () {
	checkWidget();
	return text.setFocus ();
}
public void setFont (Font font) {
	super.setFont (font);
	text.setFont (font);
	list.setFont (font);
	internalLayout ();
}
public void setForeground (Color color) {
	super.setForeground(color);
	if (text != null) text.setForeground(color);
	if (list != null) list.setForeground(color);
	if (arrow != null) arrow.setForeground(color);
}
/**
* Sets the text of an item; indexing is zero based.
*
* This operation will fail when the index is out
* of range or an item could not be changed in
* the OS.
*
* @param index the index for the item
* @param string the item
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when items is null
* @exception SWTError(ERROR_ITEM_NOT_MODIFIED)
*	when the operation fails
*/
public void setItem (int index, String string) {
	checkWidget();
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	list.setItem (index, string);
}
/**
* Sets all items.
*
* @param items the array of items
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when items is null
* @exception SWTError(ERROR_ITEM_NOT_ADDED)
*	when the operation fails
*/
public void setItems (String [] items) {
	checkWidget();
	if (items == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	int style = getStyle();
	if ((style & SWT.READ_ONLY) != 0) text.setText (""); //$NON-NLS-1$
	list.setItems (items);
}
/**
* Sets the new selection.
*
* @param selection point representing the start and the end of the new selection
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when selection is null
*/
public void setSelection (Point selection) {
	checkWidget();
	if (selection == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	text.setSelection (selection.x, selection.y);
}

/**
* Sets the widget text.
*
* @param string the widget text
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_NULL_ARGUMENT)
*	when string is null
*/
public void setText (String string) {
	checkWidget();
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	int index = list.indexOf (string);
	if (index == -1) {
		list.deselectAll ();
		text.setText (string);
		return;
	}
	text.setText (string);
	text.selectAll ();
	list.setSelection (index);
	list.showSelection ();
}
/**
* Sets the text limit.
* 
* @param limit new text limit
*
* @exception SWTError(ERROR_THREAD_INVALID_ACCESS)
*	when called from the wrong thread
* @exception SWTError(ERROR_WIDGET_DISPOSED)
*	when the widget has been disposed
* @exception SWTError(ERROR_CANNOT_BE_ZERO)
*	when limit is 0
*/
public void setTextLimit (int limit) {
	checkWidget();
	text.setTextLimit (limit);
}

public void setToolTipText (String string) {
	checkWidget();
	super.setToolTipText(string);
	arrow.setToolTipText (string);
	text.setToolTipText (string);		
}

public void setVisible (boolean visible) {
	super.setVisible(visible);
	if (!visible) popup.setVisible(false);
}

void textEvent (Event event) {
	switch (event.type) {
		case SWT.FocusIn: {
			if (hasFocus) return;
			hasFocus = true;
			if (getEditable ()) text.selectAll ();
			Event e = new Event();
			e.time = event.time;
			notifyListeners(SWT.FocusIn, e);
			break;
		}
		case SWT.FocusOut: {
			event.display.asyncExec(new Runnable() {
				public void run() {
					if (CCombo.this.isDisposed()) return;
					Control focusControl = getDisplay().getFocusControl();
					if (focusControl == list || focusControl == arrow) return;
					hasFocus = false;
					Event e = new Event();
					notifyListeners(SWT.FocusOut, e);
				}
			});
			break;
		}
		case SWT.KeyDown: {
			
			if (event.character == SWT.ESC) { // escape key cancels popup list
				dropDown (false);
			}
			if (event.character == SWT.CR) {
				dropDown (false);
				Event e = new Event();
				e.time = event.time;
				e.stateMask = event.stateMask;
				notifyListeners(SWT.DefaultSelection, e);
			}
			//At this point the widget may have been disposed.
			// If so, do not continue.
			if (isDisposed()) break;
			
			if (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN) {
				int oldIndex = getSelectionIndex ();
				if (event.keyCode == SWT.ARROW_UP) {
					select (Math.max (oldIndex - 1, 0));
				} else {
					select (Math.min (oldIndex + 1, getItemCount () - 1));
				}
		
				if (oldIndex != getSelectionIndex ()) {
					Event e = new Event();
					e.time = event.time;
					e.stateMask = event.stateMask;
					notifyListeners(SWT.Selection, e);
				}
				//At this point the widget may have been disposed.
				// If so, do not continue.
				if (isDisposed()) break;
			}
			
			// Further work : Need to add support for incremental search in 
			// pop up list as characters typed in text widget
						
			Event e = new Event();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners(SWT.KeyDown, e);
			break;
		}
		case SWT.KeyUp: {
			Event e = new Event();
			e.time = event.time;
			e.character = event.character;
			e.keyCode = event.keyCode;
			e.stateMask = event.stateMask;
			notifyListeners(SWT.KeyUp, e);
			break;
		}
		case SWT.Modify: {
			list.deselectAll ();
			Event e = new Event();
			e.time = event.time;
			notifyListeners(SWT.Modify, e);
			break;
		}
		case SWT.MouseDown: {
			if (event.button != 1) return;
			if (text.getEditable ()) return;
			boolean dropped = isDropped ();
			text.selectAll ();
			if (!dropped) setFocus ();
			dropDown (!dropped);
			break;
		}
		case SWT.MouseUp: {
			if (event.button != 1) return;
			if (text.getEditable ()) return;
			text.selectAll ();
			break;
		}
		case SWT.Traverse: {		
			switch (event.detail) {
				case SWT.TRAVERSE_RETURN:
				case SWT.TRAVERSE_ARROW_PREVIOUS:
				case SWT.TRAVERSE_ARROW_NEXT:
					// The enter causes default selection and
					// the arrow keys are used to manipulate the list contents so
					// do not use them for traversal.
					event.doit = false;
					break;
			}
			
			Event e = new Event();
			e.time = event.time;
			e.detail = event.detail;
			e.doit = event.doit;
			e.keyCode = event.keyCode;
			notifyListeners(SWT.Traverse, e);
			event.doit = e.doit;
			break;
		}
	}
}
}
