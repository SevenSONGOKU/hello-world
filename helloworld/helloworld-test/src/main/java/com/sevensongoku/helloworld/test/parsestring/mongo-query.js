db.collection('quickGoods').where({
  goodsId: 1
}).update({
  data: {
    avatar: '{{upload_file_id}}'
  }
})

db.collection('quickGoods').add({
  data: [
    {
      avatar: '',
      goodsId: 1,
      isActive: true,
      isDeleted: false,
      location: 'index-banner'
    },
    {
      avatar: '',
      goodsId: 2,
      isActive: true,
      isDeleted: false,
      location: 'index-banner'
    },
    {
      avatar: '',
      goodsId: 3,
      isActive: true,
      isDeleted: false,
      location: 'index-banner'
    },
    {
      avatar: '',
      goodsId: 4,
      isActive: true,
      isDeleted: false,
      location: 'index-banner'
    },
    {
      avatar: '',
      goodsId: 5,
      isActive: true,
      isDeleted: false,
      location: 'index-banner'
    }
  ]
})