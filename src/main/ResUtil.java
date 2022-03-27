package main;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResUtil {

    public static int thumbnailCount;

    // get rss xml file for channel
    public static void downloadXml(String channelId) {
        if (YTUtilityApp.getChannelResources != null) {
            YTUtilityApp.getChannelResources.setProgressText("   Downloading Xml");
        }
        try {
            downloadFile("https://www.youtube.com/feeds/videos.xml?channel_id=" + channelId,
                    YTUtilityApp.resDir + "/xml/" + channelId + ".xml");
            System.out.println("xml downloaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("xml download error");
        }
    }

    // download file from internet
    public static void downloadFile(String url, String filepath) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(filepath);
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        fileOutputStream.close();
        readableByteChannel.close();
    }

    // create channel object from xml file
    public static Channel parseXml(String filePath) {
        if (YTUtilityApp.getChannelResources != null) {
            YTUtilityApp.getChannelResources.setProgressText("        Parsing Xml");
        }
        thumbnailCount = 1;
        Channel channel = new Channel();

        try {
            File inputFile = new File(filePath);
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);

            Element rootElement = document.getRootElement();
            List<Element> allElementList = rootElement.getChildren(); // all elements in xml file

            // basic channel info
            channel.setChannelId(allElementList.get(2).getText());
            channel.setChannelName(allElementList.get(3).getText());

            for (int e = 7; e < allElementList.size(); e++) {

                Video video = new Video();

                // individual video element
                List<Element> videoElementList = allElementList.get(e).getChildren();

                video.setVideoId(videoElementList.get(1).getText());
                video.setVideoTitle(videoElementList.get(3).getText());
                video.setVideoPublishTime(videoElementList.get(6).getText());

                // more video info- description, stats
                List<Element> videoInfoElementList = videoElementList.get(8).getChildren();

                video.setVideoDescription(videoInfoElementList.get(3).getText());

                // video stats- views, rating
                List<Element> videoStatElementList = videoInfoElementList.get(4).getChildren();

                video.setVideoStarRating(videoStatElementList.get(0).getAttributeValue("average"));
                video.setVideoViewCount(videoStatElementList.get(1).getAttributeValue("views"));

                channel.addVideo(video);
            }

            System.out.println("xml parsed");
        } catch(JDOMException | IOException ex) {
            ex.printStackTrace();
            System.err.println("xml parse error");
        }
        return channel;
    }

    // get latest video from channel
    public static Channel quickXmlParse(String filePath) {
        Channel channel = new Channel();

        try {
            File inputFile = new File(filePath);
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document  = saxBuilder.build(inputFile);

            Element rootElement = document.getRootElement();
            List<Element> elementList = rootElement.getChildren();

            channel.setChannelId(elementList.get(2).getText());
            channel.setChannelName(elementList.get(3).getText());

            Video video = new Video();
            List<Element> videoElementList = elementList.get(7).getChildren(); // get first video info

            video.setVideoId_NoDownload(videoElementList.get(1).getText());
            video.setVideoTitle(videoElementList.get(3).getText());
            video.setVideoPublishTime(videoElementList.get(6).getText());
            channel.addVideo(video);

            System.out.println("xml quick parsed");
        } catch(JDOMException | IOException ex) {
            ex.printStackTrace();
            System.err.println("xml quick parse error");
        }
        return channel;
    }

    /** broken as of 4.14 */
    public static void webscrapePictures_Old(String channelId) {
        YTUtilityApp.getChannelResources.setProgressText("Retrieving Channel Images");
        try {
            org.jsoup.nodes.Document document = Jsoup.connect("https://www.youtube.com/channel/" + channelId).get();
//            org.jsoup.nodes.Document document = Jsoup.parse(new File("src/test.html"), "utf-8");

//            BufferedWriter htmlWriter = new BufferedWriter(new FileWriter("src/test.html"));
//            htmlWriter.write(document.html());
//            htmlWriter.close();

            Elements imageAttributes = document.getElementsByAttributeValue("id", "img-preload").first().getAllElements();

            String cover1 = "https:" + imageAttributes.get(1).attr("src");
            String cover2 = "https:" + imageAttributes.get(2).attr("src"); // full page
            String cover3 = "https:" + imageAttributes.get(3).attr("src"); // yt uses this
            String profile = imageAttributes.get(4).attr("src");

            //System.out.println(cover1+"\n"+cover2+"\n"+cover3+"\n"+profile);

            //downloadFile(cover1, YTUtilityApp.resDir + "/webscrape/" + channelId + "_cover1.jpg");
            //downloadFile(cover2, YTUtilityApp.resDir + "/webscrape/" + channelId + "_cover2.jpg");
            downloadFile(cover3, YTUtilityApp.resDir + "/webscrape/" + channelId + "_cover.jpg");
            downloadFile(profile, YTUtilityApp.resDir + "/webscrape/" + channelId + "_profile.jpg");

            System.out.println("images scraped");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("webscrape error");
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
            System.err.println("webscrape error- cannot find image urls");
        }
    }

    // get channel avatar and banner
    public static void webscrapePictures(String channelId) {
        if (YTUtilityApp.getChannelResources != null) {
            YTUtilityApp.getChannelResources.setProgressText("Retrieving Channel Images");
        }

        try {
            org.jsoup.nodes.Document document = Jsoup.connect("https://www.youtube.com/channel/" + channelId).get();

            /*org.jsoup.nodes.Document document = Jsoup.parse(new File("test.html"), "utf-8");
            BufferedWriter htmlWriter = new BufferedWriter(new FileWriter("test.html"));
            htmlWriter.write(document.html()); htmlWriter.close();*/

            Elements allScripts = document.getElementsByTag("script");

            for (org.jsoup.nodes.Element element : allScripts) {

                if (element.data().contains("\"avatar\"")) {
                    // get text that contains all the avatar links
                    Pattern profilePattern = Pattern.compile("\"avatar\":\\{\"thumbnails\":\\[(.*?)]},\"[a-z]");
                    Matcher profileMatcher = profilePattern.matcher(element.data());
                    if (profileMatcher.find()) {
                        // extract all avatar links
                        //System.out.println(profileMatcher.group(1));
                        Pattern profileUrlPattern = Pattern.compile("\\{\"url\":\"(.*?)\",\"width\":(?:[0-9][0-9][0-9]|[0-9][0-9]),\"height\":(?:[0-9][0-9][0-9]|[0-9][0-9])},?");
                        Matcher profileUrlMatcher = profileUrlPattern.matcher(profileMatcher.group(1));
                        int i = 0;
                        while (profileUrlMatcher.find()) {
                            // use the third link
                            if (i == 2) {
                                //System.out.println(profileUrlMatcher.group(1));
                                downloadFile(profileUrlMatcher.group(1), YTUtilityApp.resDir + "/webscrape/" + channelId + "_profile.jpg");
                                System.out.println("avatar downloaded");
                                break;
                            }
                            i++;
                        }
                    } else {
                        throw new IllegalStateException("webscrape error- could not find profile urls");
                    }
                }

                if (element.data().contains("\"banner\"")) {
                    // get text that contains all the banner links
                    Pattern bannerPattern = Pattern.compile("\"banner\":\\{\"thumbnails\":\\[(.*?)]},\"[a-z]");
                    Matcher bannerMatcher = bannerPattern.matcher(element.data());
                    if (bannerMatcher.find()) {
                        // extract all banner links
                        //System.out.println(bannerMatcher.group(1));
                        Pattern bannerUrlPattern = Pattern.compile("\\{\"url\":\"(.*?)\",\"width\":[0-9][0-9][0-9][0-9],\"height\":[0-9][0-9][0-9]},?");
                        Matcher bannerUrlMatcher = bannerUrlPattern.matcher(bannerMatcher.group(1));
                        int i = 0;
                        while (bannerUrlMatcher.find()) {
                            // use the fourth link
                            if (i == 3) {
                                //System.out.println(bannerUrlMatcher.group(1));
                                downloadFile(bannerUrlMatcher.group(1), YTUtilityApp.resDir + "/webscrape/" + channelId + "_cover.jpg");
                                System.out.println("banner downloaded");
                                break;
                            }
                            i++;
                        }
                    } else {
                        throw new IllegalStateException("webscrape error- could not find banner urls");
                    }
                    break;
                }
            }

            System.out.println("images scraped");
        } catch (IOException | IllegalArgumentException ex) {
            ex.printStackTrace();
            System.err.println("webscrape error- download error");
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static void createVideoHtml(String videoId, String videoName) {
        try {
            BufferedWriter htmlWriter = new BufferedWriter(new FileWriter(
                    System.getProperty("user.home") + "\\Downloads\\"+ videoId + ".html"));
            htmlWriter.write("" +
                    "<!DOCTYPE html>\n" +
                    "<html lang=\"en-US\">\n" +
                    "<meta charset=\"UTF-8\">\n" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "<title>Video Html- " + videoName + "</title>\n" +
                    "<body>\n" +
                    "<iframe width=\"1020\" height=\"574\" src=\"https://www.youtube.com/embed/" + videoId +
                    "\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" " +
                    "allowfullscreen></iframe>\n" +
                    "</body></html>"
            );
            htmlWriter.close();
            System.out.println("html created");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("html creation error");
        }
    }
}