package com.lmr.imoocmaven.imooc;

import com.lmr.imoocmaven.imooc.DownloadFile;  
import com.lmr.imoocmaven.imooc.GetInput;  
import org.json.JSONArray;  
import org.json.JSONObject;  
import org.jsoup.Jsoup;  
import org.jsoup.nodes.Document;  
import org.jsoup.nodes.Element;  
import org.jsoup.select.Elements;  
  
import java.io.File;
import java.io.FileNotFoundException;  
  
public class ImoocMain {  
  
    static int curruntCount;  
  
    static int curruntGlobalCount;  
  
    public static void main(String[] args) throws Exception { 

		int a[] = new int[] { 171 };
		int aindex=0;

        while (true) {  
            curruntCount = 0;  
            curruntGlobalCount = 0;  
  
//            int classNo = GetInput.getInputClassNo();  
            
            int classNo=a[aindex++];
            
            Document doc = Jsoup.connect("http://www.imooc.com/learn/" + classNo).get();  
            String title = doc.getElementsByTag("h2").html();  
            Elements videos = doc.select(".video a");  
            if ((title.equals("")) && (videos.size() == 0)) {  
                System.out.println("抱歉，没有该课程！\n");  
            } else {  
                int count = 0;  
                for (Element video : videos) {  
                    String[] videoNos = video.attr("href").split("/");  
                    if (videoNos.length >= 2 && videoNos[1].equals("video")) {  
                        count++;  
                    }  
                }  
  
                System.out.print("\n要下载的课程标题为【" + title + "】，");  
                System.out.println("本次要下载的视频课程有 " + count + " 节\n");  
//                int videoDef = GetInput.getInputVideoDef();  
                int videoDef = 0;   //这里默认下载超清的  
                String savePath = "D:/download/" + title + "/";  
                File file = new File(savePath);  
                file.mkdirs();  
                System.out.println("\n准备开始下载，请耐心等待…\n");  
                for (Element video : videos) {  
                    String[] videoNos = video.attr("href").split("/");  
  
                    //只下载视频  
                    if (videoNos.length > 1 && videoNos[1].equals("video")) {  
                        video.select("button").remove();  
                        String videoName = video.text().trim();  
                        videoName = videoName.substring(0, videoName.length() - 7).trim();  
                        String videoNo = videoNos[2];  
  
                        Document jsonDoc = Jsoup.connect(  
                                "http://www.imooc.com/course/ajaxmediainfo/?mid=" +  
                                        videoNo + "&mode=flash").get();  
                        String jsonData = jsonDoc.text();  
  
                        JSONObject jsonObject = new JSONObject(jsonData);  
                        JSONArray mpath = jsonObject.optJSONObject("data")  
                                .optJSONObject("result").optJSONArray("mpath");  
                        String downloadPath = mpath.getString(videoDef).trim();
//                        System.out.println(downloadPath);
                        downloadPath=downloadPath.replace("www.imooc.com", "v2.mukewang.com");
                        downloadPath=downloadPath.replace("v2.mukewang.com", "v1.mukewang.com");
//                        downloadPath=downloadPath.substring(0, downloadPath.indexOf("?"));
                        downloadPath=downloadPath.replace("L.mp4", "H.mp4");
//                        System.out.println(downloadPath);
                        
                        
                        int flag=1;
                        try {
                        	DownloadFile.downLoadFromUrl(downloadPath, videoName + ".mp4",  
                                    savePath); 
						} catch (FileNotFoundException e) {
							// TODO: handle exception
							flag=0;
							System.out.println("【" + curruntCount + "】：\t" + videoName +  
	                                " \t下载失败！");  
						}
                        
                        curruntCount += 1;  
                        if(flag==1){
                        	System.out.println("【" + curruntCount + "】：\t" + videoName +  
                                    " \t下载成功！");  
                        }
                    }  
                }  
  
                System.out.println("\n恭喜！【" + title + "】课程的所有视频已经下载完成！！！下载的文件在该程序所在目录下的download文件夹中。\n-------------------------------------------------------\n");  
            }  
        }  
    }  
}  