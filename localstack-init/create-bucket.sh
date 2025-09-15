#!/bin/bash
set -e
sleep 5
awslocal s3 mb s3://my-bucket
echo "Bucket 'my-bucket' created successfully!"
