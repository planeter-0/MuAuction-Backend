package icu.planeter.muauction.controller;

import icu.planeter.muauction.common.response.Response;
import icu.planeter.muauction.common.response.ResponseCode;
import icu.planeter.muauction.common.utils.QiniuUtils;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Planeter
 * @description: ImageController
 * @date 2021/6/11 16:45
 * @status dev
 */
@RestController
public class ImageController {
    /**
     * Upload image, return url
     *
     * @param uploadFile
     * @param request
     * @return url
     */
    @PostMapping(value = "/image/upload")
    public Response<String> uploadFile(@RequestParam("image") MultipartFile uploadFile,
                                       @RequestParam Integer type,
                                       HttpServletRequest request) {
        String url = null;
        try {
            //Rename with UUID
            String newFilename = QiniuUtils.renamePic(Objects.requireNonNull(uploadFile.getOriginalFilename()));
            //Upload to Qiniu Cloud, get url
            url = QiniuUtils.InputStreamUpload((FileInputStream) uploadFile.getInputStream(), newFilename);
            if (url.contains("error")) {
                return new Response<>(ResponseCode.UploadFailed);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response<>(ResponseCode.SUCCESS, url);
    }
}
