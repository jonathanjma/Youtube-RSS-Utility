package app;

import java.util.ArrayList;

public class Channel {

    private String channelId = "";
    private String channelUrl = "";
    private String channelName = "";

    private ArrayList<Video> videoList = new ArrayList<>();

    public void setChannelId(String channelId) {
        this.channelId = channelId;
        channelUrl = "https://www.youtube.com/channel/" + channelId;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void addVideo(Video video) {
        videoList.add(video);
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public String getChannelName() {
        return channelName;
    }

    public Video getVideo(int index) {
        return videoList.get(index);
    }

    public int getVideoListSize() {
        return videoList.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Channel)) return false;
        Channel channel = (Channel) o;

        return channelId.equals(channel.channelId) && channelName.equals(channel.channelName)
                && videoList.get(0).equals(channel.videoList.get(0));
    }
}