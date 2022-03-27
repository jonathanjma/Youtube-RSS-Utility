package main;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Video {

    private String videoId = "";
    private String videoUrl = "";
    private String videoThumbnailUrl = "";
    private String videoThumbnailPath = "";
    private String videoTitle = "";
    private LocalDateTime videoPublishTime = LocalDateTime.MIN;
    private String videoDescription = "";
    private double videoStarRating = 0;
    private long videoViewCount = 0;

    public void setVideoId(String videoId) {
        this.videoId = videoId;
        videoUrl = "https://www.youtube.com/watch?v=" + videoId;
        videoThumbnailUrl = "https://i.ytimg.com/vi/" + videoId + "/mqdefault.jpg"; // mq or maxres (hd)

        if (YTUtilityApp.getChannelResources != null) {
            YTUtilityApp.getChannelResources.setProgressText("Retrieving Thumbnail " + ResUtil.thumbnailCount + "/15");
        }
        videoThumbnailPath =  YTUtilityApp.resDir + "/thumbnails/" + videoId + ".jpg";

        try {
            ResUtil.downloadFile(videoThumbnailUrl, videoThumbnailPath);
            System.out.println("thumbnail " + ResUtil.thumbnailCount + "/15" + " downloaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("thumbnail download error");
        }
        ResUtil.thumbnailCount++;
    }

    public void setVideoId_NoDownload(String videoId) {
        this.videoId = videoId;
        videoUrl = "https://www.youtube.com/watch?v=" + videoId;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public void setVideoPublishTime(String videoPublishTime) {
        videoPublishTime = videoPublishTime.substring(0, 19);
        this.videoPublishTime = LocalDateTime.parse(videoPublishTime);
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public void setVideoStarRating(String videoStarRating) {
        this.videoStarRating = Double.parseDouble(videoStarRating);
    }

    public void setVideoViewCount(String videoViewCount) {
        this.videoViewCount = Long.parseLong(videoViewCount);
    }

    public String getVideoId() {
        return videoId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getVideoThumbnailUrl() {
        return videoThumbnailUrl;
    }

    public String getVideoThumbnailPath() {
        return videoThumbnailPath;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public LocalDateTime getVideoPublishTime_Raw() {
        return videoPublishTime;
    }

    public String getVideoPublishTime_Relative() {

        LocalDateTime now = LocalDateTime.now();
        int number;
        String unit;

        int days = (int) ChronoUnit.DAYS.between(videoPublishTime, now);
        if (days == 0) {
            int hours = (int) ChronoUnit.HOURS.between(videoPublishTime, now);
            if (hours == 1) {
                unit = "hour";
            } else if (hours > 1) {
                unit = "hours";
            } else {
                unit = "< 1 hour";
                return unit + " ago";
            }

            number = hours;
        } else if (days > 0 && days <= 7) {
            if (days == 1) {
                unit = "day";
            } else {
                unit = "days";
            }

            number = days;
        } else if (days > 7 && days <= 31) {
            int weeks = (int) ChronoUnit.WEEKS.between(videoPublishTime, now);
            if (weeks == 1) {
                unit = "week";
            } else {
                unit = "weeks";
            }

            number = weeks;
        } else if (days > 31 && days <= 365) {
            int months = (int) ChronoUnit.MONTHS.between(videoPublishTime, now);
            if (months == 1) {
                unit = "month";
            } else {
                unit = "months";
            }

            number = months;
        } else {
            int years = (int) ChronoUnit.YEARS.between(videoPublishTime, now);
            if (years == 1) {
                unit = "year";
            } else {
                unit = "years";
            }

            number = years;
        }

        return number + " " + unit + " ago";
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public double getVideoStarRating() {
        return videoStarRating;
    }

    public long getVideoViewCount_Raw() {
        return videoViewCount;
    }

    public String getVideoViewCount_Commas() {

        // could have used built in function, but eh... this works
        long localCount = videoViewCount;

        // find # of digits
        int digits = 0;
        while (localCount >= 1) {
            localCount /= 10;
            digits++;
        }

        // find # of commas needed
        int numOfComma = (digits-1) /3;

        // find first comma index
        int firstCommaIndex = digits % 3;
        if (firstCommaIndex == 0) {
            firstCommaIndex += 3;
        }

        String formattedCount = videoViewCount+"";
        if (numOfComma == 1) {
            formattedCount = formattedCount.substring(0, firstCommaIndex) + "," + formattedCount.substring(firstCommaIndex);
        } else if (numOfComma > 1) {
            // calculate index of other commas
            int commaLeft= numOfComma - 1;
            int[] commaIndices = new int[numOfComma];
            commaIndices[0] = firstCommaIndex;
            int curIndex = 1;
            while (commaLeft > 0) {
                int addAmt = 3+curIndex;
                commaIndices[curIndex] = (commaIndices[curIndex-1]-(curIndex-1))+addAmt;
                curIndex++;
                commaLeft--;
            }

            // add in all commas
            for (int i = 0; i < commaIndices.length; i++) {
                formattedCount = formattedCount.substring(0, commaIndices[i]) + "," +
                        formattedCount.substring(commaIndices[i]);
            }
        }

        return formattedCount;
    }

    public String getVideoViewCount_Short() {

        long localCount = videoViewCount;

        // find # of digits
        int digits = 0;
        if (localCount == 0) digits = 1;
        while (localCount >= 1) {
            localCount /= 10;
            digits++;
        }

        // separate digits into array
        int localDigits = digits;
        int curIndex = 0;
        ArrayList<String> digitsArray = new ArrayList<>();
        while (curIndex < digits) {
            localCount = videoViewCount / (long) Math.pow(10, localDigits-1);
            for (int n = 0; n < curIndex; n++) {
                localCount %= 10;
            }
            digitsArray.add(localCount+"");
            curIndex++;
            localDigits--;
        }

        /*
        6, 12, 145, 3.6K, 13K, 797K, 1.3M, 16M, 154M, 1.6B, 14B, 121B
        6, 12, 145, 3600, 13000, 797000, 1300000, 16000000, 154000000, 1600000000, 14000000000, 121000000000
        1:0 2:0, 3:0, 4:2, 5:3, 6:3, 7:5, 8:6, 9:6, 10:8, 11:9, 12:9
        */

        // remove unneeded digits
        if (digits > 3 && digits <= 12) {
            digitsArray.remove(digitsArray.size() - 1);
            digitsArray.remove(digitsArray.size() - 1);
            if (digits >= 5) {
                digitsArray.remove(digitsArray.size() - 1);
                if (digits >= 7) {
                    digitsArray.remove(digitsArray.size() - 1);
                    digitsArray.remove(digitsArray.size() - 1);
                    if (digits >= 8) {
                        digitsArray.remove(digitsArray.size() - 1);
                        if (digits >= 10) {
                            digitsArray.remove(digitsArray.size() - 1);
                            digitsArray.remove(digitsArray.size() - 1);
                            if (digits >= 11) {
                                digitsArray.remove(digitsArray.size() - 1);
                            }
                        }
                    }
                }
            }
        }

        // add decimal point
        if (digits == 4 || digits == 7 || digits == 10) {
            digitsArray.add(1, ".");
        }

        // add in abbreviation
        String symbol = "";
        if (digits > 3 && digits <= 6) {
            symbol = "K";
        } else if (digits > 6 && digits <= 9) {
            symbol = "M";
        } else if (digits > 9 && digits <= 12) {
            symbol = "B";
        }

        // convert array to string
        String shortenedCount ="";
        for (String str : digitsArray) {
            //noinspection StringConcatenationInLoop
            shortenedCount += str;
        }
        shortenedCount += symbol;

        return shortenedCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Video)) return false;
        Video video = (Video) o;

        return videoId.equals(video.videoId) && videoTitle.equals(video.videoTitle);
    }
}