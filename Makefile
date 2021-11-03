# Setup variables
AWS_ACCOUNT_ID="123456789012"
LOCAL_DOCKER_IMAGE="java-spring-boot-ecs-fargate-redis-caching_web"
REPOSITORY_NAME="ecr-demo"
AWS_REGION="eu-north-1"
REPO_URL="$(AWS_ACCOUNT_ID).dkr.ecr.$(AWS_REGION).amazonaws.com/$(REPOSITORY_NAME)"
IMAGE_TAG="1.0"
CONTAINER_FULL_URL="$(REPO_URL):$(IMAGE_TAG)"

# Colors
ccblue = \033[0;96m
ccend = \033[0m

deploy-local:
	@echo "[*] $(ccblue)Deploying Locally $*$(ccend)"
	docker-compose up

deploy-aws:
	@echo "[*] $(ccblue)Deploying to AWS $*$(ccend)"
	@${MAKE} create-infra
	@${MAKE} push-app
	@${MAKE} deploy-app
create-infra:
	@echo "[*] $(ccblue)Deploying base Infrastructure stack to AWS $*$(ccend)"
	sam build -t cfn/infra/main.yaml
	sam deploy --stack-name infra-stack --resolve-s3 --capabilities CAPABILITY_IAM CAPABILITY_AUTO_EXPAND
push-app:
	@echo "[*] $(ccblue)Pushing java-spring-boot-ecs-fargate-redis-caching_web image to AWS ECR $*$(ccend)"
	aws ecr get-login-password --region $(AWS_REGION) | docker login --username AWS --password-stdin $(REPO_URL)
	docker tag $(LOCAL_DOCKER_IMAGE) $(CONTAINER_FULL_URL)
	docker push $(CONTAINER_FULL_URL)
deploy-app:
	@echo "[*] $(ccblue)Deploying Java Application stack to AWS $*$(ccend)"
	sam build -t cfn/applications/java-spring-boot-service.yaml
	sam deploy --stack-name java-spring-boot --resolve-s3 --capabilities CAPABILITY_NAMED_IAM