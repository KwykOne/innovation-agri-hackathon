"""djangotest URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from djangotestapp import views, viewsml



# Change FOR DL/ML in the urlpatterns

urlpatterns = [
    path('admin/', admin.site.urls),
    path('',views.test,name='test'),
    path('data/',views.sendData,name='sendData'),
    path('webpage/',views.webpage,name='webpage'),
    path('post/',views.FormView.as_view({'post':'form'})),
    path('img/',views.TestView.as_view({'post':'form'}))
    # path('post/',views.postData,name='postData'), # For DL
    # path('post/',viewsml.postData,name='postData'), # For ML
]
