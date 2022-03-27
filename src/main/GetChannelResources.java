package main;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class GetChannelResources implements Runnable {

    private String channelId;
    private SimpleStringProperty progressText;
    private YTUtilityApp app;
    public boolean oneChannel;

    public GetChannelResources(String channelId, SimpleStringProperty loadingProgressText, YTUtilityApp app) {
        this(loadingProgressText, app);
        this.channelId = channelId;
        oneChannel = true;
    }

    public GetChannelResources(SimpleStringProperty loadingProgressText, YTUtilityApp app) {
        progressText = loadingProgressText;
        this.app = app;
        oneChannel = false;
    }

    @Override
    public void run() {
        if (oneChannel) {
            ResUtil.downloadXml(channelId);
            Channel channel = ResUtil.parseXml(YTUtilityApp.resDir + "/xml/" + channelId + ".xml");
            ResUtil.webscrapePictures(channel.getChannelId());
            Platform.runLater(() -> app.channelScreen(channel));
        } else {
            ArrayList<String[]> quickChannelInfo = QuickLookupInfo.getQuickLookupInfo();
            ArrayList<Boolean> updateList = new ArrayList<>();

            int count = 0;
            for (String[] info : quickChannelInfo) {
                Channel channel1 = ResUtil.quickXmlParse(YTUtilityApp.resDir + "/xml/" + info[1] + ".xml");
                ResUtil.downloadXml(info[1]);
                Channel channel2 = ResUtil.quickXmlParse(YTUtilityApp.resDir + "/xml/" + info[1] + ".xml");
                if (!channel1.equals(channel2)) {
                    updateList.add(true);
                } else {
                    updateList.add(false);
                }
                count++;
                setProgressText("Updating channel " + count + "/" + quickChannelInfo.size());
            }

            System.out.println("done");
            Platform.runLater(() -> app.showUpdates(updateList));
        }
    }

    public void setProgressText(String text) {
        Platform.runLater(() -> progressText.set(text));
    }
}