from bs4 import BeautifulSoup
import requests
import json
from xhtml2pdf import pisa

def SearchNovels(searchTerm):
    searchTerm = searchTerm.replace(" ", "+")
    print(searchTerm)
    searchString = "https://boxnovel.com/?s={terms}&post_type=wp-manga".format(terms = searchTerm)
    result = requests.get(searchString)
    doc = BeautifulSoup(result.text, "html.parser")
    content = doc.find(role="tabpanel", class_="c-tabs-item")
    novels = content.find_all(class_="c-tabs-item__content")
    novelsInfo = []
    for novel in novels:
        novelInfo = {}
        novelInfo["url"] = novel.find("a")["href"]
        novelInfo["coverImgUrl"] = novel.find("img")["data-src"]
        novelInfo["title"] = novel.find(class_="post-title").find("a").getText()
        novelsInfo.append(novelInfo)
    print(json.dumps(novelsInfo, indent=4))

def GetChapterContent(url, filename):
    result = requests.get(url)

    doc = BeautifulSoup(result.text, "html.parser")

    content = doc.find(class_="cha-words")
    if content is None:
        content = doc.find(class_="reading-content")
    content.find_all(["script"])

    for elem in content.find_all(["script"]):
        elem.parent.parent.decompose()
    content = "<html><body>{content}</body></html>".format(content=content)
    result_file = open("temp/{fname}.pdf".format(fname=filename), "w+b")
    pisa_status = pisa.CreatePDF(content, dest=result_file)
    result_file.close()
    print("{fname} done downloading".format(fname=filename))

def GetChapterList(url):
    result = requests.post(
                url = "{url}ajax/chapters/".format(url=url),
                headers={
                    'X-Requested-With': 'XMLHttpRequest'
                }
    )
    doc = BeautifulSoup(result.text, "html.parser")

    chapterList = doc.find(class_="listing-chapters_wrap").find("ul").find_all("li")

    chapters = []
    for chapter in chapterList:
        chapterInfo = {}
        chapterInfo["title"] = chapter.find("a").getText().replace("\n", "")
        chapterInfo["url"] = chapter.find("a")["href"]
        chapters.append(chapterInfo)
    return chapters
    


def GetNovelInfo(url):
    result = requests.get(url)
    doc = BeautifulSoup(result.text, "html.parser")
    #summary_content = doc.find(class_="summary_content")
    novel_img = doc.find(class_="summary_image").find("img")
    novel_desc = doc.find(id="editdescription")
    for elem in novel_desc.find_all(["script"]):
        elem.parent.parent.decompose()
    
    novelInfo = {}
    novelInfo["img"] = novel_img
    novelInfo["desc"] = novel_desc


#SearchNovels("Lord of the mysteries")
chapters = GetChapterList("https://boxnovel.com/novel/lord-of-the-mysteries-boxnovel/")
for i in range(10):
    print(chapters[i])
    GetChapterContent(chapters[i]["url"], "chapter-{num}".format(num=i))
#GetChapterContent("https://boxnovel.com/novel/lord-of-the-mysteries-boxnovel/chapter-1/")