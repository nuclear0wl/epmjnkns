FROM python:3.6.1
COPY sample_app/ /app
WORKDIR /app
RUN pip install -r requirements.txt
EXPOSE 5050
ENTRYPOINT ["python"]
CMD ["app.py"]
