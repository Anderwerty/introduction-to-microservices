#!/bin/bash
set -e
sleep 5
awslocal s3 mb s3://permanent-bucket
awslocal s3 mb s3://staging-bucket
echo "Bucket 'my-bucket' created successfully!"
