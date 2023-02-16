package com.mcreater.amcl.pages.dialogs.commons;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.mcreater.amcl.Launcher;
import com.mcreater.amcl.controls.AdvancedScrollPane;
import com.mcreater.amcl.pages.dialogs.AbstractDialog;
import com.mcreater.amcl.pages.interfaces.Fonts;
import com.mcreater.amcl.theme.ThemeManager;
import com.mcreater.amcl.util.FXUtils;
import com.mcreater.amcl.util.StringUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.ChangeEvent;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mcreater.amcl.util.FXUtils.ColorUtil.reverse;

public class PopupMessage {
    public static final ObservableList<MessageItem> messages = FXCollections.observableArrayList();
    private static MessageDialog dialog;
    private static BooleanProperty unreaded = new SimpleBooleanProperty(false);
    static {
        messages.addListener((ListChangeListener<MessageItem>) c -> unreaded.set(true));
    }
    public enum MessageTypes {
        LABEL,
        HYPERLINK,
        BUTTON
    }
    public static Labeled createMessage(String text, MessageTypes type, @Nullable EventHandler<Event> handler) {
        return createMessage(text, type, handler, Color.BLACK);
    }
    public static Labeled createMessage(String text, MessageTypes type, @Nullable EventHandler<Event> handler, Paint paint){
        Labeled l;
        switch (type){
            case HYPERLINK:
                Hyperlink link = new Hyperlink(text);
                link.setFont(Fonts.t_f);
                link.setTextFill(paint);
                if (handler != null) link.setOnAction(handler::handle);
                l = link;
                break;
            case BUTTON:
                JFXButton button = new JFXButton(text);
                button.setFont(Fonts.t_f);
                button.setTextFill(paint);
                if (handler != null) button.setOnAction(handler::handle);
                l = button;
                break;
            default:
            case LABEL:
                Label label = new Label(text);
                label.setFont(Fonts.t_f);
                label.setTextFill(paint);
                l = label;
                break;
        }
        Labeled finalL = l;
        Platform.runLater(() -> createMessageInternal(finalL));
        ThemeManager.loadNodeAnimations(l);
        return finalL;
    }
    private static Labeled createMessageInternal(Labeled node) {
        ThemeManager.addLis((observable, oldValue, newValue) -> node.setTextFill(reverse(newValue)));
        messages.add(new MessageItem(java.util.Date.from(Instant.now(Clock.systemDefaultZone())), node));
        return node;
    }

    public static class MessageItem extends HBox {
        public final java.util.Date timestamp;
        public final Labeled content;
        Label time;
        JFXButton delete;
        public MessageItem(java.util.Date timestamp, Labeled content) {
            this.timestamp = timestamp;
            this.content = content;
            content.setWrapText(true);
            delete = new JFXButton();
            FXUtils.ControlSize.set(delete, 30, 30);
            FXUtils.ControlSize.setWidth(content, 330 - 30);
            ThemeManager.addLis((observable, oldValue, newValue) -> delete.setGraphic(Launcher.getSVGManager().delete(ThemeManager.createPaintBinding(), 30, 30)));

            time = new Label(StringUtils.toStringDate(timestamp));
            time.setFont(Fonts.t_f);

            getChildren().addAll(new VBox(content, time), delete);
            setAlignment(Pos.TOP_LEFT);

            FXUtils.ControlSize.setWidth(this, 330);
        }

        public JFXButton getDeleteButton() {
            return delete;
        }
    }

    public static void showDialog() {
        unreaded.set(false);
        if (dialog != null) dialog.close();
        dialog = new MessageDialog();
        dialog.Create();
    }

    public static boolean getUnreaded() {
        return unreaded.get();
    }

    public static void addUnreadedListener(ChangeListener<Boolean> handler) {
        unreaded.addListener(handler);
    }

    private static class MessageDialog extends AbstractDialog {
        JFXButton close;
        VBox messages;
        public MessageDialog() {
            JFXDialogLayout layout = new JFXDialogLayout();

            Label title = setFont(new Label(Launcher.languageManager.get("ui.popupmessage.messageque")), Fonts.s_f);

            close = new JFXButton(Launcher.languageManager.get("ui.dialogs.information.ok.name"));
            close.setFont(Fonts.t_f);
            close.setOnAction(event -> close());

            messages = new VBox();
            messages.getChildren().addAll(PopupMessage.messages);
            reloadMessages();
            PopupMessage.messages.addListener((ListChangeListener<MessageItem>) c -> {
                c.next();
                List<KeyValue> value = c.getRemoved().stream()
                        .map((Function<MessageItem, KeyValue>) messageItem -> new KeyValue(messageItem.opacityProperty(), 1))
                        .collect(Collectors.toList());

                List<KeyValue> value2 = c.getRemoved().stream()
                        .map((Function<MessageItem, KeyValue>) messageItem -> new KeyValue(messageItem.opacityProperty(), 0))
                        .collect(Collectors.toList());

                KeyFrame frame = new KeyFrame(Duration.ZERO, "", null, value);
                KeyFrame frame1 = new KeyFrame(Duration.millis(350), "", null, value2);

                Timeline timeline = new Timeline(frame, frame1);
                timeline.setOnFinished(event -> {
                    messages.getChildren().clear();
                    messages.getChildren().addAll(PopupMessage.messages);
                    reloadMessages();
                });
                timeline.playFromStart();
            });

            AdvancedScrollPane scrollPane = new AdvancedScrollPane(350, 300, messages, false);
            scrollPane.setId("opc");

            ThemeManager.loadNodeAnimations(close, title);
            layout.setBody(scrollPane);
            layout.setHeading(title);
            layout.setActions(close);
            setContent(layout);
        }
        private void reloadMessages() {
            PopupMessage.messages.forEach(ThemeManager::loadSingleNodeAnimate);
            PopupMessage.messages.forEach(messageItem -> messageItem.getDeleteButton().setOnAction(event2 -> PopupMessage.messages.remove(messageItem)));
        }
    }
}
