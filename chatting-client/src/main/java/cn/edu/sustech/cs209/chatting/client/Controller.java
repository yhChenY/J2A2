package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.MessageType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class Controller implements Initializable {

  @FXML
  ListView<Message> chatContentList;
  ObservableList<Message> chatMessages = FXCollections.observableList(new ArrayList<>());
  @FXML
  ListView<ChatTarget> chatList;
  ObservableList<ChatTarget> chatTargets = FXCollections.observableList(new ArrayList<>());
  @FXML
  TextArea inputArea;
  @FXML
  Label currentUsername;
  @FXML
  Label currentOnlineCnt;
  String username;
  ClientService clientService;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    Dialog<String> dialog = new TextInputDialog();
    dialog.setTitle("Login");
    dialog.setHeaderText(null);
    dialog.setContentText("Username:");

    Optional<String> input = dialog.showAndWait();
    if (input.isPresent() && !input.get().isEmpty() && !input.get().contains(",")) {
            /*
               TODO: Check if there is a user with the same name among the currently logged-in users,
                     if so, ask the user to change the username
             */
//            System.out.println(666);
      username = input.get();
//            System.out.println(777);
      clientService = new ClientService(chatTargets, this);
//            System.out.println(888);
      currentOnlineCnt.setText("initial");
      Thread t = new Thread(clientService);
      t.start();
      try {
        System.out.println("Get online user list");
        clientService.refreshOnlineUsers();
      } catch (InterruptedException | IOException e) {
        throw new RuntimeException(e);
      }
      for (String s : clientService.getOnlineUsers()) {
        System.out.println(s);
        if (s.equals(username)) {
          System.out.println("Exist username!");
          try {
            clientService.quit();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          Platform.exit();
          return;
        }
      }
      try {
        clientService.login(username);
        FetchMessageThread fmt = new FetchMessageThread(clientService, this);
        fmt.start();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      System.out.println("Invalid username " + input + ", contains ','");
      Platform.exit();
    }
    chatList.setItems(chatTargets);
    Callback<ListView<ChatTarget>, ListCell<ChatTarget>> cellFactory = new Callback<ListView<ChatTarget>, ListCell<ChatTarget>>() {
      @Override
      public ListCell<ChatTarget> call(ListView<ChatTarget> listView) {
        return new ListCell<ChatTarget>() {
          @Override
          protected void updateItem(ChatTarget item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
              setText(item.getTitle());
            } else {
              setText(null);
            }
          }
        };
      }
    };

    chatContentList.setItems(chatMessages);
    chatContentList.setCellFactory(new MessageCellFactory());
    chatList.setCellFactory(cellFactory);
    chatList.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
        ChatTarget ct = chatList.getSelectionModel().getSelectedItem();
//                System.out.println(chatContentList.getItems().size());
        chatContentList.setItems(ct.getMessages());
//                System.out.println(chatContentList.getItems().size());
//                chatContentList.getItems().addAll(ct.getMessages());
      }
    });
  }

  @FXML
  public void createPrivateChat() {
    AtomicReference<String> user = new AtomicReference<>();

    Stage stage = new Stage();
    ComboBox<String> userSel = new ComboBox<>();

//        userSel.getItems().addAll("Item 1", "Item 2", "Item 3");
    for (String s : clientService.getOnlineUsers()) {
      if (!s.equals(username)) {
        userSel.getItems().add(s);
      }
    }

    Button okBtn = new Button("OK");
    okBtn.setOnAction(e -> {
      user.set(userSel.getSelectionModel().getSelectedItem());
      stage.close();
    });

    HBox box = new HBox(20);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new Insets(20, 20, 20, 20));
    box.getChildren().addAll(userSel, okBtn);
    stage.setScene(new Scene(box));
    stage.showAndWait();
    // TODO: if the current user already chatted with the selected user, just open the chat with that user
    boolean contains = false;
    for (ChatTarget ct : chatTargets) {
      if (ct.getTitle().equals(user.get())) {
        contains = true;
        // select
        System.out.println("Chat exist");
        chatList.getSelectionModel().select(ct);
        break;
      }
    }
    // TODO: otherwise, create a new chat item in the left panel, the title should be the selected user's name
    if (!contains) {
      chatTargets.add(new ChatTarget(user.get(), user.get()));
    }
  }

  /**
   * A new dialog should contain a multi-select list, showing all user's name.
   * You can select several users that will be joined in the group chat, including yourself.
   * <p>
   * The naming rule for group chats is similar to WeChat:
   * If there are > 3 users: display the first three usernames, sorted in lexicographic order, then use ellipsis with the number of users, for example:
   * UserA, UserB, UserC... (10)
   * If there are <= 3 users: do not display the ellipsis, for example:
   * UserA, UserB (2)
   */
  @FXML
  public void createGroupChat() {
  }

  /**
   * Sends the message to the <b>currently selected</b> chat.
   * <p>
   * Blank messages are not allowed.
   * After sending the message, you should clear the text input field.
   */
  @FXML
  public void doSendMessage() throws IOException {
    // TODO
    String sentBy = username;
    String sendTo = chatList.getSelectionModel().getSelectedItem().getTitle();
    sendTo = "["+sendTo+"]";
    String data = inputArea.getText();
    if (data != null && !data.isEmpty() && !data.trim().isEmpty()) {
      clientService.sendMessage(new Message(MessageType.SEND, System.currentTimeMillis(), sentBy, sendTo, data));
    }
    inputArea.clear();
  }

  /**
   * You may change the cell factory if you changed the design of {@code Message} model.
   * Hint: you may also define a cell factory for the chats displayed in the left panel, or simply override the toString method.
   */
  private class MessageCellFactory implements Callback<ListView<Message>, ListCell<Message>> {
    @Override
    public ListCell<Message> call(ListView<Message> param) {
      return new ListCell<Message>() {

        @Override
        public void updateItem(Message msg, boolean empty) {
          super.updateItem(msg, empty);
          if (empty || Objects.isNull(msg)) {
            setText(null);
            setGraphic(null);
            return;
          }

          HBox wrapper = new HBox();
          Label nameLabel = new Label(msg.getSentBy());
          Label msgLabel = new Label(msg.getData());

          nameLabel.setPrefSize(50, 20);
          nameLabel.setWrapText(true);
          nameLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

          if (username.equals(msg.getSentBy())) {
            wrapper.setAlignment(Pos.TOP_RIGHT);
            wrapper.getChildren().addAll(msgLabel, nameLabel);
            msgLabel.setPadding(new Insets(0, 20, 0, 0));
          } else {
            wrapper.setAlignment(Pos.TOP_LEFT);
            wrapper.getChildren().addAll(nameLabel, msgLabel);
            msgLabel.setPadding(new Insets(0, 0, 0, 20));
          }

          setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
          setGraphic(wrapper);
        }
      };
    }
  }
}
