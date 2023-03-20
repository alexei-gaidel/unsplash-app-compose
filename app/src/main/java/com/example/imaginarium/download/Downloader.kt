package com.example.imaginarium.download

interface Downloader {
    fun downLoadFile(url: String): Long
}