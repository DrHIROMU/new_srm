FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html/*

COPY dist/ /usr/share/nginx/html/

#（可選）自訂 nginx.conf
# COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
