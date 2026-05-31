FROM node:22-alpine

WORKDIR /app

COPY package*.json ./
RUN npm ci --omit=dev

COPY src ./src
COPY env ./env

ENV NODE_ENV=production
ENV PORT=8088

EXPOSE 8088

CMD ["node", "src/server.mjs"]
