package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsGridFsTest {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    //删除文件
    @Test
    public void testDelFile() throws IOException {
        //根据文件id删除fs.files和fs.chunks中的记录
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is("5f696cf81f71cb35a41ea586")));
    }

    @Test
    public void queryFile() throws IOException {
        String fileId = "5f69729a1f71cb3de8a5e869";
        //根据id查询文件
        GridFSFile gridFSFile =
                gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream =
                gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFsResource，用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //获取流中的数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), StandardCharsets.UTF_8);
        System.out.println(s);
    }

    @Test
    public void testGridFsTemplate() throws FileNotFoundException {
        File file = new File("E:\\Code\\JAVA\\21-xcEdu\\XcEduCode03\\test-freemarker\\src\\main\\resources\\templates\\index_banner.ftl");
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectId objectId = gridFsTemplate.store(fileInputStream, "index_banner.ftl");
        String id = objectId.toString();
        System.out.println(id);
    }

    @Test
    public void testStore2() throws FileNotFoundException {
        File file = new File("E:\\Code\\JAVA\\21-xcEdu\\XcEduCode03\\test-freemarker\\src\\test\\resources\\templates\\course.ftl");
        FileInputStream inputStream = new FileInputStream(file);
        //保存模版文件内容
        ObjectId objectId = gridFsTemplate.store(inputStream, "课程详情模板文件", "");
        String fileId = objectId.toString();
        System.out.println(fileId);
    }
}
