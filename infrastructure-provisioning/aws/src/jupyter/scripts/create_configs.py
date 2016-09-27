#!/usr/bin/python
import boto3
from fabric.api import *
import argparse
import os

parser = argparse.ArgumentParser()
parser.add_argument('--bucket', type=str, default='')
parser.add_argument('--cluster_name', type=str, default='')
parser.add_argument('--dry_run', type=str, default='false')
args = parser.parse_args()

def prepare():
    local('rm -rf /srv/*')
    local('mkdir -p /srv/hadoopconf/')
    local('mkdir -p /opt/jars/')
    result = os.path.exists("/opt/jars/emr-4.3.0/aws")
    return result

def jars(args):
    print "Downloading jars..."
    s3client = boto3.client('s3')
    s3resource = boto3.resource('s3')
    get_files(s3client, s3resource, 'jars/', args.bucket, '/opt/')

def yarn(args):
    print "Downloading yarn configuration..."
    s3client = boto3.client('s3')
    s3resource = boto3.resource('s3')
    get_files(s3client, s3resource, 'config/', args.bucket, '/srv/hadoopconf/')
    local('mv /srv/hadoopconf/config/* /srv/hadoopconf/')

def pyspark_kernel(args):
    local('mkdir -p /home/ubuntu/.local/share/jupyter/kernels/pyspark_' + args.cluster_name + '/')
    kernel_path = "/home/ubuntu/.local/share/jupyter/kernels/pyspark_" + args.cluster_name + "/kernel.json"
    template_file = "/tmp/pyspark_emr_template.json"
    with open(kernel_path, 'w') as out:
        with open(template_file) as tpl:
            for line in tpl:
                out.write(line.replace('CLUSTER', args.cluster_name))

def toree_kernel(args):
    local('mkdir -p /home/ubuntu/.local/share/jupyter/kernels/toree_' + args.cluster_name + '/')
    kernel_path = "/home/ubuntu/.local/share/jupyter/kernels/toree_" + args.cluster_name + "/kernel.json"
    template_file = "/tmp/toree_emr_template.json"
    with open(kernel_path, 'w') as out:
        with open(template_file) as tpl:
            for line in tpl:
                out.write(line.replace('CLUSTER', args.cluster_name))

def get_files(s3client, s3resource, dist, bucket, local):
    s3list = s3client.get_paginator('list_objects')
    for result in s3list.paginate(Bucket=bucket, Delimiter='/', Prefix=dist):
        if result.get('CommonPrefixes') is not None:
            for subdir in result.get('CommonPrefixes'):
                get_files(s3client, s3resource, subdir.get('Prefix'), bucket, local)
        if result.get('Contents') is not None:
            for file in result.get('Contents'):
                if not os.path.exists(os.path.dirname(local + os.sep + file.get('Key'))):
                     os.makedirs(os.path.dirname(local + os.sep + file.get('Key')))
                s3resource.meta.client.download_file(bucket, file.get('Key'), local + os.sep + file.get('Key'))

def spark_defaults():
    local('cp /tmp/spark-defaults_template.conf /opt/spark/conf/spark-defaults.conf')

if __name__ == "__main__":
    if args.dry_run == 'true':
        parser.print_help()
    else:
        result = prepare()
        if result == False :
            jars(args)
        yarn(args)
        pyspark_kernel(args)
        toree_kernel(args)
        spark_defaults()