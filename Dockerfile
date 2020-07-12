# This Dockerfile creates a static build image for CI
# https://github.com/menny/docker_android
FROM menny/android:1.13.6 

# install OS packages
RUN apt-get --quiet update --yes
RUN apt-get --quiet install --yes ruby ruby-dev
# We use this for xxd hex->binary
RUN apt-get --quiet install --yes vim-common
# install FastLane
COPY Gemfile.lock .
COPY Gemfile .
RUN gem install bundler
RUN bundle install