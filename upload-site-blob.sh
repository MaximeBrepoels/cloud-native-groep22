#!/bin/bash

echo "Building front-end..."
cd cloud-groep22_frontend
npm run build:static

# Check if build succeeded
if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

cd ..

storage_account="$AZURE_STORAGE_ACCOUNT"
container_name="$AZURE_STORAGE_CONTAINER"
sas_token="$AZURE_STORAGE_SAS_TOKEN"

# Use the built files, not source code
local_frontend_folder="cloud-groep22_frontend/out"

# Check if build folder (/out) exists
if [ ! -d "$local_frontend_folder" ]; then
    echo "Error: Build folder $local_frontend_folder not found."
    exit 1
fi

find "$local_frontend_folder" -type f | while read -r file_path; do
    if [ -f "$file_path" ]; then
        # Extract the relative path from the local folder
        relative_path=${file_path#$local_frontend_folder/}

        # Construct the Blob Storage URL for the file
        blob_url="https://$storage_account.blob.core.windows.net/$container_name/$relative_path?$sas_token"

        # Set Content-Type based on file extension
        extension="${file_path##*.}"
        content_type=""
        case "$extension" in
            "css") content_type="text/css" ;;
            "js") content_type="application/javascript" ;;
            "html") content_type="text/html" ;;
            "svg") content_type="image/svg+xml" ;;
            "png") content_type="image/png" ;;
            "ico") content_type="image/x-icon" ;;
            *) content_type=$(file --mime-type -b "$file_path") ;;
        esac

        echo "Uploading: $relative_path"

        # Upload the file to Blob Storage using curl
        curl -X PUT -T "$file_path" -H "x-ms-blob-type: BlockBlob" -H "Content-Type: $content_type" "$blob_url"

        if [ $? -eq 0 ]; then
            echo "Uploaded Succeeded!: $relative_path"
        else
            echo "Failed: $relative_path"
        fi
    fi
done

echo "Upload complete!"