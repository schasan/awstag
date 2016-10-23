package com.dbg.cms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.*;
import com.amazonaws.regions.Region;
//import com.amazonaws.services.ec2.AmazonEC2;
//import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.*;
import com.amazonaws.services.ec2.model.*;

//https://forums.aws.amazon.com/thread.jspa?threadID=60733

public class tagMine {
	public static void main(String[] args) throws IOException {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("dbg-shared-sandbox").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (C:\\Users\\schuma1\\.aws\\credentials), and is in valid format.",
							e);
		}
		AmazonEC2 ec2 = new AmazonEC2Client(credentials);
		Region euCentral1 = Region.getRegion(Regions.EU_CENTRAL_1);
		ec2.setRegion(euCentral1);

		List<String> users = Arrays.asList("ah240", "wiestil", "dw580", "viradan");

		List<Tag> replaceTags = Arrays.asList(new Tag("CostCenter", "6431"),
				new Tag("Application", "T7"),
				new Tag("Project", "AWS-Rollout"),
				new Tag("Environment", "None"),
				new Tag("Application", "None"),
				new Tag("Confidentiality", "Highly confidential"));

		List<String> allRessources = new ArrayList<String>();

		for (String user : users) {
			List<String> valuesT1 = Arrays.asList(user);
			Filter filter1 = new Filter("tag:Creator", valuesT1);

			System.out.println("===== Instances created by " + user + " =====");
			DescribeInstancesRequest requestInstance = new DescribeInstancesRequest();
			DescribeInstancesResult resultInstances = ec2.describeInstances(requestInstance.withFilters(filter1));
			List<Reservation> reservations = resultInstances.getReservations();
			for (Reservation reservation : reservations) {
				List<Instance> instances = reservation.getInstances();
				for (Instance instance : instances) {
					System.out.print(instance.getInstanceId());
					allRessources.add(instance.getInstanceId());
					List<Tag> tags = instance.getTags();
					for (Tag tag : tags) {
						String key = tag.getKey();
						String value = tag.getValue();
						System.out.print(", " + key + ": " + value);
					}
					System.out.println();
				}
			}
			
			System.out.println("===== Volumes created by " + user + " =====");
			DescribeVolumesRequest requestVolumes = new DescribeVolumesRequest();
			DescribeVolumesResult resultVolumes = ec2.describeVolumes(requestVolumes.withFilters(filter1));
			List<Volume> volumes = resultVolumes.getVolumes();
			for (Volume volume : volumes) {
				System.out.print(volume.getVolumeId());
				allRessources.add(volume.getVolumeId());
				List<Tag> tags = volume.getTags();
				for (Tag tag : tags) {
					String key = tag.getKey();
					String value = tag.getValue();
					System.out.print(", " + key + ": " + value);
				}
				System.out.println();
			}
			
			System.out.println("===== Network ACLs created by " + user + " =====");
			DescribeNetworkAclsRequest describeNetworkAclsRequest = new DescribeNetworkAclsRequest();
			DescribeNetworkAclsResult resultNetworkAcls = ec2.describeNetworkAcls(describeNetworkAclsRequest.withFilters(filter1));
			List<NetworkAcl> networkAcls = resultNetworkAcls.getNetworkAcls();
			for (NetworkAcl networkAcl : networkAcls) {
				System.out.print(networkAcl.getNetworkAclId());
				allRessources.add(networkAcl.getNetworkAclId());
				List<Tag> tags = networkAcl.getTags();
				for (Tag tag : tags) {
					String key = tag.getKey();
					String value = tag.getValue();
					System.out.print(", " + key + ": " + value);
				}
				System.out.println();
			}

			System.out.println("===== Network Interfaces created by " + user + " =====");
			DescribeNetworkInterfacesRequest describeNetworkInterfacesRequest = new DescribeNetworkInterfacesRequest();
			DescribeNetworkInterfacesResult resultNetworkInterfaces = ec2.describeNetworkInterfaces(describeNetworkInterfacesRequest.withFilters(filter1));
			List<NetworkInterface> networkInterfaces = resultNetworkInterfaces.getNetworkInterfaces();
			for (NetworkInterface networkInterface : networkInterfaces) {
				System.out.print(networkInterface.getNetworkInterfaceId());
				allRessources.add(networkInterface.getNetworkInterfaceId());
				List<Tag> tags = networkInterface.getTagSet();
				for (Tag tag : tags) {
					String key = tag.getKey();
					String value = tag.getValue();
					System.out.print(", " + key + ": " + value);
				}
				System.out.println();
			}
		}
		System.out.println(allRessources);
		
		CreateTagsRequest ctr = new CreateTagsRequest();
		ctr.setTags(replaceTags);
		ctr.withResources(allRessources);
		CreateTagsResult ret = ec2.createTags(ctr);
		System.out.println("http status: " + ret.getSdkHttpMetadata().getHttpStatusCode());
	}
}
