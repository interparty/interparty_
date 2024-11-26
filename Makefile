up:
	wsl docker run -td --name redis_interparty -p 6379:6379 redis:alpine

down:
	wsl docker stop redis_interparty && wsl docker rm redis_interparty

ping:
	wsl redis-cli ping

clear:
	wsl sudo systemctl stop redis && wsl docker container prune -f