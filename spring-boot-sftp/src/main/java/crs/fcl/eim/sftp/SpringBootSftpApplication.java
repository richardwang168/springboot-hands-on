package crs.fcl.eim.sftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import crs.fcl.eim.sftp.service.FilesStorageService;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class SpringBootSftpApplication implements CommandLineRunner {

	  @Resource
	  FilesStorageService storageService;

	  public static void main(String[] args) {
	    SpringApplication.run(SpringBootSftpApplication.class, args);
	  }

	  @Override
	  public void run(String... arg) throws Exception {
	    storageService.deleteAll();
	    storageService.init();
	  }
	}