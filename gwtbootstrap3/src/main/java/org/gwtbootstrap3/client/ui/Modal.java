package org.gwtbootstrap3.client.ui;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

import org.gwtbootstrap3.client.shared.event.*;
import org.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import org.gwtbootstrap3.client.ui.base.modal.ModalContent;
import org.gwtbootstrap3.client.ui.base.modal.ModalDialog;
import org.gwtbootstrap3.client.ui.constants.Attributes;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.html.Div;

/**
 * Modal dialog.
 * <p/>
 * <h3>UiBinder example</h3>
 * <p/>
 * <pre>
 * {@code
 *     <b:Modal title="Important information" b:id="modal1">
 *         <b:ModalBody>
 *             <g:HTML>Lorem ipsum...</g:HTML>
 *         </b:ModalBody>
 *         <b:ModalFooter>
 *             <b:Button type="PRIMARY">Do something</b:Button>
 *             <b:Button type="DANGER" dismiss="MODAL">Close</b:Button>
 *         </b:ModalFooter>
 *     </b:Modal>
 *     <b:Button target="#modal1" toggle="MODAL">Show modal</b:Button>
 * }
 * </pre>
 * <p/>
 * It's also possible to specify a custom modal header:
 * <p/>
 * <pre>
 * {@code
 *     <b:Modal>
 *         <b:ModalHeader>
 *             <g:HTML>
 *                 <h4>Custom header</h4>
 *             </g:HTML>
 *         </b:ModalHeader>
 *         ...
 *     </b:Modal>
 * }
 * </pre>
 *
 * @author Sven Jacobs
 * @author Joshua Godi
 * @see ModalHeader
 * @see ModalBody
 * @see ModalFooter
 * @see org.gwtbootstrap3.client.shared.event.ModalShowEvent
 * @see org.gwtbootstrap3.client.shared.event.ModalShownEvent
 * @see org.gwtbootstrap3.client.shared.event.ModalHideEvent
 * @see org.gwtbootstrap3.client.shared.event.ModalHiddenEvent
 */
public class Modal extends Div implements IsClosable {

    private final static String TOGGLE = "toggle";
    private final static String HIDE = "hide";
    private final static String SHOW = "show";

    private final ModalContent content = new ModalContent();
    private final ModalDialog dialog = new ModalDialog();
    private ModalHeader header = new ModalHeader();

    private boolean hideOtherModals = false;

    public Modal() {
        setStyleName(Styles.MODAL);

        content.add(header);
        dialog.add(content);

        add(dialog);
    }

    public void setWidth(final String width) {
        dialog.setWidth(width);
    }
    
    public void setSize(ModalSize size) {
        StyleHelper.addUniqueEnumStyleName(dialog, ModalSize.class, size);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        hide();
        bindJavaScriptEvents(getElement());
    }
    
    /**
     * For the case were a modal is added DOM, navigating away from page while
     * the modal was shown would result in the static background remaining on
     * the on the screen. See: SWC-2028. To address this issue we added the
     * override the onUnload() method and call both hide() and
     * unbindAllHandlers(). This addresses the issue.
     */
    @Override
    protected void onUnload(){
        super.onUnload();
        hide();
        unbindAllHandlers(getElement());
    }
    
    @Override
    public void add(final Widget w) {
        // User can supply own ModalHeader
        if (w instanceof ModalHeader) {
            header.removeFromParent();
            header = (ModalHeader) w;
        }

        if (w instanceof ModalComponent) {
            content.add(w);
        } else {
            super.add(w);
        }
    }

    public void setTitle(final String title) {
        header.setTitle(title);
    }

    @Override
    public void setClosable(final boolean closable) {
        header.setClosable(closable);
    }

    @Override
    public boolean isClosable() {
        return header.isClosable();
    }

    /**
     * If set to true, when the modal is shown it will force hide all other modals
     *
     * @param hideOtherModals - true to force hide other modals, false to keep them shown
     */
    public void setHideOtherModals(final boolean hideOtherModals) {
        this.hideOtherModals = hideOtherModals;
    }

    /**
     * If set to true, will remove the modal from the DOM completely and unbind any events to the modal
     *
     * @param removeOnHide - true to remove modal and unbind events on hide, false to keep it in the DOM
     */
    public void setRemoveOnHide(final boolean removeOnHide) {
        if (removeOnHide) {
            addHiddenHandler(new ModalHiddenHandler() {
                @Override
                public void onHidden(final ModalHiddenEvent evt) {
                    unbindAllHandlers(getElement());
                    removeFromParent();
                }
            });
        }
    }

    /**
     * If set Modal will fade in/out.
     *
     * @param fade If {@code true} modal will fade in/out
     */
    public void setFade(final boolean fade) {
        if (fade) {
            addStyleName(Styles.FADE);
        } else {
            removeStyleName(Styles.FADE);
        }
    }

    /**
     * Sets backdrop of modal.
     *
     * @param backdrop Backdrop of modal
     * @see org.gwtbootstrap3.client.ui.constants.ModalBackdrop
     */
    public void setDataBackdrop(final ModalBackdrop backdrop) {
        if (backdrop != null) {
            getElement().setAttribute(Attributes.DATA_BACKDROP, backdrop.getBackdrop());
        } else {
            getElement().removeAttribute(Attributes.DATA_BACKDROP);
        }
    }

    public void setDataKeyboard(final boolean keyboard) {
        getElement().setAttribute(Attributes.DATA_KEYBOARD, Boolean.toString(keyboard));
    }

    public void toggle() {
        modal(getElement(), TOGGLE);
    }

    public void show() {
        checkIsAttached();
        modal(getElement(), SHOW);
    }

    public void hide() {
        modal(getElement(), HIDE);
    }

    /**
     * The default behavior of the close button is to unconditionally close the
     * modal using the DOM attribute:
     * <p>
     * data-dismiss="modal"
     * <p>
     * To override this behavior a ClickHandler can be added to the close
     * button. Note: This method will remove the data-dismiss attribute from the
     * DOM element.
     * 
     * @param handler
     */
    public HandlerRegistration addCloseHanlder(ClickHandler handler){
        return header.addCloseHnalder(handler);
    }
    
    public HandlerRegistration addShowHandler(final ModalShowHandler modalShowHandler) {
        return addHandler(modalShowHandler, ModalShowEvent.getType());
    }

    public HandlerRegistration addShownHandler(final ModalShownHandler modalShownHandler) {
        return addHandler(modalShownHandler, ModalShownEvent.getType());
    }

    public HandlerRegistration addHideHandler(final ModalHideHandler modalHideHandler) {
        return addHandler(modalHideHandler, ModalHideEvent.getType());
    }

    public HandlerRegistration addHiddenHandler(final ModalHiddenHandler modalHiddenHandler) {
        return addHandler(modalHiddenHandler, ModalHiddenEvent.getType());
    }

    /**
     * Can be override by subclasses to handle Modal's "show" event however it's
     * recommended to add an event handler to the modal.
     *
     * @param evt Event
     * @see org.gwtbootstrap3.client.shared.event.ModalShowEvent
     */
    protected void onShow(final Event evt) {
        if (hideOtherModals) {
            hideOtherModals();
        }
        fireEvent(new ModalShowEvent(this, evt));
    }

    /**
     * Can be override by subclasses to handle Modal's "shown" event however
     * it's recommended to add an event handler to the modal.
     *
     * @param evt Event
     * @see org.gwtbootstrap3.client.shared.event.ModalShownEvent
     */
    protected void onShown(final Event evt) {
        fireEvent(new ModalShownEvent(this, evt));
    }

    /**
     * Can be override by subclasses to handle Modal's "hide" event however it's
     * recommended to add an event handler to the modal.
     *
     * @param evt Event
     * @see org.gwtbootstrap3.client.shared.event.ModalHideEvent
     */
    protected void onHide(final Event evt) {
        fireEvent(new ModalHideEvent(this, evt));
    }

    /**
     * Can be override by subclasses to handle Modal's "hidden" event however
     * it's recommended to add an event handler to the modal.
     *
     * @param evt Event
     * @see org.gwtbootstrap3.client.shared.event.ModalHiddenEvent
     */
    protected void onHidden(final Event evt) {
        fireEvent(new ModalHiddenEvent(this, evt));
    }

    private void checkIsAttached() {
        if (!this.isAttached()) {
            RootPanel.get().add(this);
        }
    }

    private native void bindJavaScriptEvents(final Element e) /*-{
        var target = this;
        var $modal = $wnd.jQuery(e);

        $modal.on('show.bs.modal', function (evt) {
            target.@org.gwtbootstrap3.client.ui.Modal::onShow(Lcom/google/gwt/user/client/Event;)(evt);
        });

        $modal.on('shown.bs.modal', function (evt) {
            target.@org.gwtbootstrap3.client.ui.Modal::onShown(Lcom/google/gwt/user/client/Event;)(evt);
        });

        $modal.on('hide.bs.modal', function (evt) {
            target.@org.gwtbootstrap3.client.ui.Modal::onHide(Lcom/google/gwt/user/client/Event;)(evt);
        });

        $modal.on('hidden.bs.modal', function (evt) {
            target.@org.gwtbootstrap3.client.ui.Modal::onHidden(Lcom/google/gwt/user/client/Event;)(evt);
        });
    }-*/;

    private native void modal(final Element e, final String arg) /*-{
        $wnd.jQuery(e).modal(arg);
    }-*/;

    // Will iterate over all the modals, if they are visible it will hide them
    private native void hideOtherModals() /*-{
        $wnd.jQuery('.modal.in').modal('hide');
    }-*/;

    // Unbinds all the handlers
    private native void unbindAllHandlers(final Element e) /*-{
        var $e = $wnd.jQuery(e);
        $e.off('show.bs.modal');
        $e.off('shown.bs.modal');
        $e.off('hide.bs.modal');
        $e.off('hidden.bs.modal');
    }-*/;
}
