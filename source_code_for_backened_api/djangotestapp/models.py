from django.db import models
# Global settings
BASE_DIR = "/home/ubuntu/djangotest/djangotestapp/"
# LOG_DIR = f"{BASE_DIR}log/"
# Create your models here.
from django.utils import timezone

class CaptureImage(models.Model):
    img_id = models.AutoField(primary_key=True)
    img = models.TextField(blank=True,null=True)
    timestamp = models.DateTimeField(auto_now_add=True)
    class Meta:
        db_table = 'CaptureImage'

