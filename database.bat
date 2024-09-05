@REM Doesn't work from bat, run manually in terminal
cd database
docker build -t quartz-postgres .
docker run -d -p 5432:5432 --name quartz-postgres quartz-postgres