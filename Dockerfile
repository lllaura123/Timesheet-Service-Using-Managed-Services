FROM docker.lej.eis.network/library/node:12.16.1-alpine As builder
WORKDIR /usr/src/app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build --prod
FROM docker.lej.eis.network/library/nginx:1.15.8-alpine
COPY --from=builder /usr/src/app/dist/timesheet-approve-service/ /usr/share/nginx/html