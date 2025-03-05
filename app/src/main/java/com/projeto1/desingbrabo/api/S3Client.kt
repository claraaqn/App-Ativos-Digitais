package com.projeto1.desingbrabo.api

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions

object S3Client {

    private val ACCESS_KEY = ConfigHelper.getProperty("wasabi.access.key")
    private val SECRET_KEY = ConfigHelper.getProperty("wasabi.secret.key")
    private val ENDPOINT = ConfigHelper.getProperty("wasabi.endpoint")

    val client: AmazonS3Client by lazy {
        val credentials = BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
        val s3Client = AmazonS3Client(credentials)
        s3Client.setEndpoint(ENDPOINT)
        s3Client
    }
}