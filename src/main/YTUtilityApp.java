package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

public class YTUtilityApp extends Application {

    private BorderPane mainPane = new BorderPane();
    private HBox quickChannelBox;
    private ArrayList<Label> channelLbs;
    private ArrayList<Group> videoGroups;
    private int paneLength;

    public static GetChannelResources getChannelResources;
    public final static String resDir = System.getProperty("java.io.tmpdir") + "/YT_Utility";

    public final boolean useBlockPeriod = false; /////

    @Override
    public void start(Stage primaryStage) {

        if (!new File(YTUtilityApp.resDir).exists()) {
            new File(YTUtilityApp.resDir).mkdir();
            new File(YTUtilityApp.resDir + "/xml").mkdir();
            new File(YTUtilityApp.resDir + "/thumbnails").mkdir();
            new File(YTUtilityApp.resDir + "/webscrape").mkdir();
        }

        loadFonts();
        homeScreen();

        Scene scene = new Scene(mainPane, 650, 530);
        primaryStage.setTitle("Youtube Utility");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("main/res/icon.png"));
        primaryStage.show();
    }

    public void homeScreen() {

        VBox startElements = new VBox(5);
        startElements.setPadding(new Insets(5,5,5,5));
        startElements.setAlignment(Pos.CENTER);
        startElements.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        Label title = new Label("Youtube Utility");
        title.setFont(customFont(30));

        HBox manualInput = new HBox(5);
        manualInput.setPadding(new Insets(5,5,5,5));
        manualInput.setAlignment(Pos.CENTER);

        Label manual = new Label("Enter channel id: ");
        manual.setFont(customFont());
        TextField manualField = new TextField();
        manualField.setFont(customFont());
        manualField.setPrefWidth(210);

        Button enter = new Button("Enter");
        enter.setFont(customFont());
        enter.setOnMouseClicked(e -> {
            if (manualField.getText().length() == 24) { // all channel ids are 24 chars long
                System.out.println(manualField.getText());
                loadingScreen(manualField.getText());
            }
        });
        if (inBlockPeriod()) {
            enter.setDisable(true);
        }

        manualInput.getChildren().addAll(manual, manualField, enter);

        HBox quickLookup = new HBox(10);
        quickLookup.setPadding(new Insets(5,5,5,5));
        quickLookup.setAlignment(Pos.CENTER);

        Label quick = new Label("Quick Lookup");
        quick.setFont(customFont(23));

        Button update = new Button("\uD83D\uDD03");
        update.setFont(customFont());
        update.setOnMouseClicked(e -> loadingScreen(""));
        quickLookup.getChildren().addAll(quick, update);

        ArrayList<String[]> quickChannelInfo = QuickLookupInfo.getQuickLookupInfo();

        if (quickChannelBox == null) {
            quickChannelBox = new HBox(5);
            quickChannelBox.setPadding(new Insets(5, 5, 5, 5));
            quickChannelBox.setAlignment(Pos.CENTER);

            channelLbs = new ArrayList<>();

            double channelsPerCol = 9;
            int numOfVBox = (int) Math.ceil(quickChannelInfo.size() / channelsPerCol);

            int curIndex = 0;
            int maxColIndex = 0;
            for (int b = 0; b < numOfVBox; b++) {
                VBox channelBox = new VBox(5);
                channelBox.setPadding(new Insets(5, 5, 5, 5));
                channelBox.setAlignment(Pos.CENTER);

                if (maxColIndex + channelsPerCol < quickChannelInfo.size()) {
                    maxColIndex += channelsPerCol;
                } else {
                    maxColIndex = quickChannelInfo.size();
                }

                for (; curIndex < maxColIndex; curIndex++) {
                    Label channel = new Label(quickChannelInfo.get(curIndex)[0]);
                    channel.setFont(customFont());
                    channel.setId(quickChannelInfo.get(curIndex)[1]);
                    channel.setOnMouseClicked(e -> {
                        System.out.println(channel.getId());
                        loadingScreen(channel.getId());
                    });
                    channelLbs.add(channel);

                    Tooltip tooltip = new Tooltip();
                    tooltip.setFont(customFont());
                    tooltip.setOnShown(e -> {
                        tooltip.setText("Loading...");
                        new Thread(() -> {
                            ResUtil.downloadXml(channel.getId());
                            Channel channelObj = ResUtil.quickXmlParse(YTUtilityApp.resDir + "/xml/" + channel.getId() + ".xml");
                            Platform.runLater(() -> tooltip.setText("Latest: " + channelObj.getVideo(0).getVideoTitle()
                                    + " (" + channelObj.getVideo(0).getVideoPublishTime_Relative() + ")"));
                        }).start();
                    });
                    channel.setTooltip(tooltip);

                    channelBox.getChildren().add(channel);
                }
                quickChannelBox.getChildren().add(channelBox);
            }
        }

        Label videoHtml = new Label("Video Html Creator");
        videoHtml.setFont(customFont(23));

        HBox html = new HBox(5);
        html.setPadding(new Insets(5,5,5,5));
        html.setAlignment(Pos.CENTER);

        Label htmlDir = new Label("Enter video id: ");
        htmlDir.setFont(customFont());
        TextField htmlInput = new TextField();
        htmlInput.setFont(customFont());
        html.setPrefWidth(210);

        Button create = new Button("Enter");
        create.setFont(customFont());
        create.setOnMouseClicked(e -> {
            if (htmlInput.getText().length() == 11) { // all video ids are 11 chars long
                System.out.println(htmlInput.getText());
                ResUtil.createVideoHtml(htmlInput.getText(), htmlInput.getText());
                getHostServices().showDocument(
                        System.getProperty("user.home") + "\\Downloads\\" + htmlInput.getText() + ".html"
                );
            }
        });
        if (inBlockPeriod()) {
            create.setDisable(true);
        }
        html.getChildren().addAll(htmlDir, htmlInput, create);

        startElements.getChildren().addAll(title, manualInput, quickLookup, quickChannelBox, videoHtml, html);

        mainPane.setTop(new HBox()); // clear channel header
        mainPane.setCenter(startElements);
    }

    public void showUpdates(ArrayList<Boolean> updateList) {
        homeScreen();

        int numUpdates = Collections.frequency(updateList, true);
        if (numUpdates != updateList.size()) {
            for (int i = 0; i < channelLbs.size(); i++) {
                if (updateList.get(i)) {
                    channelLbs.get(i).setTextFill(Color.ORANGERED);
                    System.out.println(channelLbs.get(i).getText() + " has update");
                } else {
                    channelLbs.get(i).setTextFill(Color.BLACK);
                }
            }
        }

        if (numUpdates == 0) {
            System.out.println("No channels have updates");
        } else {
            System.out.println("All channels have updates");
        }
    }

    public void loadingScreen(String channelId) {
        Pane loadingPane = new Pane();
        mainPane.setCenter(loadingPane);
        loadingPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        ImageView view = new ImageView(new Image("main/res/loading.gif"));
        view.setX(125); view.setY(75);

        Text loadingText = new Text("Loading Resources");
        loadingText.setX(200); loadingText.setY(350);
        loadingText.setFont(customFont(23));
        SimpleStringProperty loadProgressText = new SimpleStringProperty("Loading Resources");
        loadingText.textProperty().bind(loadProgressText);

        loadingPane.getChildren().addAll(view, loadingText);

        if (!channelId.isEmpty()) {
            getChannelResources = new GetChannelResources(channelId, loadProgressText, this);
            Thread getResourcesThread = new Thread(getChannelResources, "Get Resources");
            getResourcesThread.start();
        } else {
            GetChannelResources updateAll = new GetChannelResources(loadProgressText, this);
            Thread updateAllThread = new Thread(updateAll, "Update All");
            updateAllThread.start();
        }
    }

    public void channelScreen(Channel channel) {

        HBox nameBox = createHeader(channel);
        nameBox.setSpacing(5);
        nameBox.setPadding(new Insets(5,5,5,5));

        Group channelElements = createChannelElementsGroup(channel);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(channelElements);
        scrollPane.pannableProperty().set(true);
        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: rgb(255,255,255);");

        paneLength = 1550;
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            // vValue is % of length of bar, so if more info clicked, value decreases
            if (scrollPane.getVvalue() * paneLength > 180) {
                mainPane.setTop(nameBox);
            } else {
                mainPane.setTop(null);
            }
        });

        //scrollPane.setOnMouseClicked(e-> System.out.println(e.getX()+" "+ e.getY()));

        mainPane.setCenter(scrollPane);
    }

    public HBox createHeader(Channel channel) {

        HBox nameBox = new HBox();

        ImageView homeImg = new ImageView(new Image("main/res/home.png", 30, 30,
                true, true));
        homeImg.setOnMouseClicked(e -> homeScreen());

        Region region1 = new Region();
        HBox.setHgrow(region1, Priority.ALWAYS);

        Label channelName = new Label(channel.getChannelName());
        channelName.setFont(customFont(23));
        channelName.setOnMouseClicked(e -> getHostServices().showDocument(channel.getChannelUrl()));

        Region region2 = new Region();
        HBox.setHgrow(region2, Priority.ALWAYS);

        nameBox.getChildren().addAll(homeImg, region1, channelName, region2);

        return nameBox;
    }

    public Group createChannelElementsGroup(Channel channel) {
        Group channelElements = new Group();

        String coverPath = YTUtilityApp.resDir + "/webscrape/" + channel.getChannelId() + "_cover.jpg";
        String profilePath = YTUtilityApp.resDir + "/webscrape/" + channel.getChannelId() + "_profile.jpg";

        ImageView coverImage = new ImageView(new Image(new File(coverPath).toURI().toString(),
                635, 105, true, true));
        coverImage.setX(0); coverImage.setY(0);

        ImageView profileImage = new ImageView(new Image(new File(profilePath).toURI().toString(),
                100, 100, true, true));
        profileImage.setX(35); profileImage.setY(80);

        Text channelName = new Text(channel.getChannelName());
        channelName.setOnMouseClicked(e -> getHostServices().showDocument(channel.getChannelUrl()));
        channelName.setX(160); channelName.setY(155);
        channelName.setFont(customFont(30));

        Button homeBtn = new Button("", new ImageView(
                new Image("main/res/home.png", 20, 20, true, true)));
        homeBtn.setOnMouseClicked(e -> homeScreen());
        homeBtn.setLayoutX(5); homeBtn.setLayoutY(5);

        channelElements.getChildren().addAll(coverImage, profileImage, channelName, homeBtn);

        videoGroups = new ArrayList<>();

        int yOffset = 0;
        for (int v = 0; v < channel.getVideoListSize(); v++) {
            Video video = channel.getVideo(v);

            Group videoGroup = new Group();
            videoGroup.setId(v+"");

            ImageView videoThumbnail = new ImageView(new Image(new File(video.getVideoThumbnailPath()).toURI().toString(),
                    200, 100, true, true));
            videoThumbnail.setX(10); videoThumbnail.setY(200+yOffset); //125
            videoThumbnail.setOnMouseClicked(e -> getHostServices().showDocument(video.getVideoUrl()));

            Text videoTitle = new Text(video.getVideoTitle());
            videoTitle.setX(195); videoTitle.setY(215+yOffset);
            videoTitle.setFont(customFont());

            Text videoPublishTime = new Text(video.getVideoPublishTime_Relative()+"");
            videoPublishTime.setX(195); videoPublishTime.setY(240+yOffset);
            videoPublishTime.setFont(customFont());

            Text videoViewCount = new Text(video.getVideoViewCount_Short() + " views");
            videoViewCount.setX(195); videoViewCount.setY(265+yOffset);
            videoViewCount.setFont(customFont());

            Text moreInfo = new Text("More >>");
            moreInfo.setId("false");
            moreInfo.setX(195); moreInfo.setY(290+yOffset);
            moreInfo.setFont(customFont());

            Text rating = new Text("Rating: " + video.getVideoStarRating() + "/5.0");
            rating.setX(20); rating.setY(330+yOffset);
            rating.setVisible(false);
            rating.setFont(customFont());

            ProgressBar ratingBar = new ProgressBar(video.getVideoStarRating()/5);
            ratingBar.setPrefWidth(300);
            ratingBar.setLayoutX(20); ratingBar.setLayoutY(340+yOffset);
            ratingBar.setVisible(false);

            Button createHtmlBtn = new Button("Create Video HTML");
            createHtmlBtn.setLayoutX(20); createHtmlBtn.setLayoutY(370+yOffset);
            createHtmlBtn.setVisible(false);
            createHtmlBtn.setFont(customFont());
            createHtmlBtn.setOnMouseClicked(e -> {
                ResUtil.createVideoHtml(video.getVideoId(), video.getVideoTitle());
                getHostServices().showDocument(
                        System.getProperty("user.home") + "\\Downloads\\" + video.getVideoId() + ".html"
                );
            });
            if (inBlockPeriod()) {
                createHtmlBtn.setDisable(true);
            }

            int linesInDescription = video.getVideoDescription().split("\n").length + 2;

            TextFlow description = new TextFlow();
            Text header = new Text("Description:"); header.setFont(customFont());
            description.getChildren().addAll(header, new Text("\n"), new Text("\n"));
            for (String line : video.getVideoDescription().split("\n")) {
                for (String word : line.split("\\s")) {
                    Text wordTxt = new Text(word + " ");
                    if (word.contains("http")) {
                        wordTxt.setFill(Color.rgb(0, 123, 255));
                        wordTxt.setOnMouseClicked(e -> getHostServices().showDocument(wordTxt.getText()));
                        wordTxt.setOnMouseEntered(e -> wordTxt.setUnderline(true));
                        wordTxt.setOnMouseExited(e -> wordTxt.setUnderline(false));
                    }
                    wordTxt.setFont(customFont());
                    description.getChildren().add(wordTxt);
                }
                description.getChildren().add(new Text("\n"));
            }
            description.setLayoutX(20); description.setLayoutY(420+yOffset);
            description.setVisible(false);

            //Text description = new Text("Description:\n\n" + video.getVideoDescription());

            int moreInfoLines = linesInDescription + 6;

            videoGroup.getChildren().addAll(videoThumbnail,videoTitle,videoPublishTime,videoViewCount,moreInfo,
                    rating, ratingBar, createHtmlBtn, description);
            channelElements.getChildren().add(videoGroup);
            videoGroups.add(videoGroup);

            moreInfo.setOnMouseClicked(e -> {
                int groupId = Integer.parseInt(moreInfo.getParent().getId());
                //System.out.println(groupId + " " + moreInfo.getId());
                if (!Boolean.parseBoolean(moreInfo.getId())) {
                    paneLength += moreInfoLines*17;
                    shiftGroups(groupId + 1, true, moreInfoLines);

                    rating.setVisible(true);
                    ratingBar.setVisible(true);
                    createHtmlBtn.setVisible(true);
                    description.setVisible(true);
                    moreInfo.setText("More ⬎");
                    moreInfo.setId("true");
                } else {
                    paneLength -= moreInfoLines*17;
                    shiftGroups(groupId + 1, false, moreInfoLines);

                    rating.setVisible(false);
                    ratingBar.setVisible(false);
                    createHtmlBtn.setVisible(false);
                    description.setVisible(false);
                    moreInfo.setText("More >>");
                    moreInfo.setId("false");
                }
            });

            yOffset += 125;
        }

        return channelElements;
    }

    public void shiftGroups(int startingGroup, boolean shiftDown, int lines) {
        double spacing = lines * 17;
        for (int g = startingGroup; g < videoGroups.size(); g++) {
            Group group = videoGroups.get(g);
            if (shiftDown) {
                group.setLayoutY(group.getLayoutY() + spacing);
            } else {
                group.setLayoutY(group.getLayoutY() - spacing);
            }
        }
    }

    public void loadFonts() {
        // font families: Open Sans, Open Sans ExtraBold, Open Sans Light, Open Sans SemiBold
        Font.loadFont(getClass().getResource("/main/res/OpenSans/OpenSans-Bold.ttf").toExternalForm(), 14);
        Font.loadFont(getClass().getResource("/main/res/OpenSans/OpenSans-BoldItalic.ttf").toExternalForm(), 14);
        Font.loadFont(getClass().getResource("/main/res/OpenSans/OpenSans-ExtraBold.ttf").toExternalForm(), 14);
        Font.loadFont(getClass().getResource("/main/res/OpenSans/OpenSans-ExtraBoldItalic.ttf").toExternalForm(), 14);
        Font.loadFont(getClass().getResource("/main/res/OpenSans/OpenSans-Italic.ttf").toExternalForm(), 14);
        Font.loadFont(getClass().getResource("/main/res/OpenSans/OpenSans-Light.ttf").toExternalForm(), 14);
        Font.loadFont(getClass().getResource("/main/res/OpenSans/OpenSans-LightItalic.ttf").toExternalForm(), 14);
        Font.loadFont(getClass().getResource("/main/res/OpenSans/OpenSans-Regular.ttf").toExternalForm(), 14);
        Font.loadFont(getClass().getResource("/main/res/OpenSans/OpenSans-SemiBold.ttf").toExternalForm(), 14);
        Font.loadFont(getClass().getResource("/main/res/OpenSans/OpenSans-SemiBoldItalic.ttf").toExternalForm(), 14);
    }

    public Font customFont() {
        return Font.font("Open Sans");
    }

    public Font customFont(int size) {
        return Font.font("Open Sans", size);
    }

    public boolean inBlockPeriod() {
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        LocalTime now = LocalTime.now();

        if (!useBlockPeriod) return false;

        if ((day.getValue() == 6 || day.getValue() == 7) && now.isAfter(LocalTime.of(11, 30))
                && now.isBefore(LocalTime.of(19, 0))) {
            System.out.println("in weekend bock period");
            return true;
        } else if (day.getValue() < 6 &&
                now.isAfter(LocalTime.of(16, 30)) && now.isBefore(LocalTime.of(20, 30))) {
            System.out.println("in weekday bock period");
            return true;
        }
        return false;
    }
}