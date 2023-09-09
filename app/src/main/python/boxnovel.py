from bs4 import BeautifulSoup
import requests
import json

def SearchNovels(searchTerm):
    searchTerm = searchTerm.replace(" ", "+")
    searchString = "https://boxnovel.com/?s={terms}&post_type=wp-manga".format(terms = searchTerm)
    print(searchString)
    result = requests.get(searchString)
    doc = BeautifulSoup(result.text, "html.parser")
    content = doc.find(role="tabpanel", class_="c-tabs-item")
    novels = content.find_all(class_="c-tabs-item__content")
    novelsInfo = []
    for novel in novels:
        novelInfo = {}
        novelInfo["url"] = novel.find("a")["href"]
        novelInfo["coverImg"] = requests.get(novel.find("img")["data-src"]).content
        novelInfo["title"] = novel.find(class_="post-title").find("a").getText().strip('\n')
        novelsInfo.append(novelInfo)
    return novelsInfo

def GetChapterContent(url):
    result = requests.get(url)

    doc = BeautifulSoup(result.text, "html.parser")

    content = doc.find(class_="cha-words")
    if content is None:
        content = doc.find(class_="reading-content")
    content.find_all(["script"])

    for elem in content.find_all(["script"]):
        elem.parent.parent.decompose()
    content = "<html><body>{content}</body></html>".format(content=content)
    return content

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
    novel_img = requests.get(doc.find(class_="summary_image").find("img")["data-src"]).content
    novel_desc = doc.find(id="editdescription")
    if novel_desc is None:
        novel_desc = doc.find(class_="description-summary").find(class_="c_000")

    for elem in novel_desc.find_all(["script"]):
        elem.parent.parent.decompose()
    for br in novel_desc.find_all("br"):
        br.replace_with("\n")
        
    novelTitle = doc.find(class_="post-title").find("h1").text.strip('\n')

    novelInfo = {}
    novelInfo["img"] = novel_img
    novelInfo["desc"] = novel_desc.get_text()
    novelInfo["title"] = novelTitle
    return novelInfo
