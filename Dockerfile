FROM runmymind/docker-android-sdk:latest

# Installing build tools
RUN apt-get update && \
  apt-get install -y \
  build-essential \
  ruby \
  ruby-dev

# Installing fastlane
RUN gem install fastlane

# Installing bundle
RUN gem install bundle

# Install gradle
RUN wget https://services.gradle.org/distributions/gradle-6.1.1-bin.zip
RUN mkdir /opt/gradle
RUN unzip -d /opt/gradle gradle-6.1.1-bin.zip
RUN export PATH=$PATH:/opt/gradle/gradle-6.1.1/bin

# Work directory
WORKDIR /app