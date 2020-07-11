FROM runmymind/docker-android-sdk:latest

# install OS packages
RUN apt-get --quiet update --yes
RUN apt-get --quiet install --yes ruby ruby-dev
# We use this for xxd hex->binary
RUN apt-get --quiet install --yes vim-common
# install FastLane
COPY Gemfile.lock .
COPY Gemfile .
RUN gem update --system
RUN gem install bundles
RUN bundle install
